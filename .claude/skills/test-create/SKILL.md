---
name: test-create
description: Generate tests using JUnit 5/Kotest + Mockito/MockK. Supports BehaviorSpec, DescribeSpec, StringSpec styles.
---

# Test Generator

Test generation target: $ARGUMENTS

## Test Location

```
src/test/
├── java/com/example/app/
│   └── domain/{domain}/
│       ├── application/usecase/     # UseCase unit tests
│       ├── domain/service/          # Service unit tests
│       ├── presentation/            # Controller tests
│       └── fixture/                 # Test fixtures
└── kotlin/com/example/app/
    └── domain/{domain}/
        ├── application/usecase/     # UseCase unit tests
        ├── domain/service/          # Service unit tests
        ├── presentation/            # Controller tests
        └── fixture/                 # Test fixtures
```

## Test Styles

### Java - JUnit 5 + Mockito

```java
@ExtendWith(MockitoExtension.class)
class UserGetServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserGetService userGetService;

    @Test
    @DisplayName("should return user when exists")
    void findById_whenUserExists_shouldReturnUser() {
        // given
        User user = UserTestFixture.createUser(1L, "test@example.com");
        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(user));

        // when
        User result = userGetService.findById(1L);

        // then
        assertThat(result).isEqualTo(user);
        verify(userRepository).findByIdAndDeletedAtIsNull(1L);
    }

    @Test
    @DisplayName("should throw exception when user not found")
    void findById_whenUserNotFound_shouldThrowException() {
        // given
        when(userRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userGetService.findById(999L))
            .isInstanceOf(UserNotFoundException.class);
    }
}
```

### Kotlin - DescribeSpec (Recommended for Services)

```kotlin
class UserGetServiceTest : DescribeSpec({
    val userRepository = mockk<UserRepository>()
    val service = UserGetService(userRepository)

    describe("findById") {
        context("when user exists") {
            it("should return user") {
                // given
                val user = UserTestFixture.createUser()
                every { userRepository.findByIdAndDeletedAtIsNull(1L) } returns user

                // when
                val result = service.findById(1L)

                // then
                result shouldBe user
                verify { userRepository.findByIdAndDeletedAtIsNull(1L) }
            }
        }

        context("when user does not exist") {
            it("should throw UserNotFoundException") {
                // given
                every { userRepository.findByIdAndDeletedAtIsNull(999L) } returns null

                // when & then
                shouldThrow<UserNotFoundException> {
                    service.findById(999L)
                }
            }
        }
    }
})
```

### Kotlin - BehaviorSpec (BDD style for complex logic)

```kotlin
class CreateUserUsecaseTest : BehaviorSpec({
    val userSaveService = mockk<UserSaveService>()
    val userMapper = mockk<UserMapper>()
    val usecase = CreateUserUsecase(userSaveService, userMapper)

    Given("a valid create user request") {
        val request = CreateUserRequest(name = "John", email = "john@example.com")
        val user = UserTestFixture.createUser()
        val savedUser = user.copy(id = 1L)

        every { userMapper.toEntity(request) } returns user
        every { userSaveService.save(any()) } returns savedUser

        When("creating user") {
            val result = usecase.execute(request)

            Then("user should be created with ID") {
                result.id shouldBe 1L
            }

            Then("save service should be called") {
                verify { userSaveService.save(any()) }
            }
        }
    }

    Given("a request with duplicate email") {
        val request = CreateUserRequest(name = "John", email = "existing@example.com")

        every { userMapper.toEntity(request) } returns UserTestFixture.createUser()
        every { userSaveService.save(any()) } throws UserAlreadyExistsException()

        When("creating user") {
            Then("should throw UserAlreadyExistsException") {
                shouldThrow<UserAlreadyExistsException> {
                    usecase.execute(request)
                }
            }
        }
    }
})
```

### Kotlin - StringSpec (Simple tests)

```kotlin
class UserValidationTest : StringSpec({
    "name longer than 100 characters should throw exception" {
        val longName = "a".repeat(101)
        shouldThrow<IllegalArgumentException> {
            User.create(name = longName, email = "test@example.com")
        }
    }

    "empty email should throw exception" {
        shouldThrow<IllegalArgumentException> {
            User.create(name = "John", email = "")
        }
    }

    "valid user should be created successfully" {
        val user = User.create(name = "John", email = "john@example.com")
        user.name shouldBe "John"
        user.email shouldBe "john@example.com"
    }
})
```

## Controller Test

### Java

```java
@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateUserUsecase createUserUsecase;

    @Test
    @DisplayName("POST /api/v1/users - should create user")
    void createUser_shouldReturnCreatedUser() throws Exception {
        // given
        CreateUserRequest request = new CreateUserRequest("John", "john@example.com");
        UserResponse response = UserResponse.builder().id(1L).name("John").build();
        when(createUserUsecase.execute(any())).thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("John"));
    }
}
```

### Kotlin

```kotlin
@WebMvcTest(UserController::class)
@Import(SecurityConfig::class)
class UserControllerTest : DescribeSpec() {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockkBean
    lateinit var createUserUsecase: CreateUserUsecase

    init {
        describe("POST /api/v1/users") {
            context("with valid request") {
                it("should return 200 OK with created user") {
                    // given
                    val request = CreateUserRequest(name = "John", email = "john@example.com")
                    val response = UserResponse(id = 1L, name = "John")
                    every { createUserUsecase.execute(any()) } returns response

                    // when & then
                    mockMvc.perform(
                        post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    )
                        .andExpect(status().isOk)
                        .andExpect(jsonPath("$.id").value(1))
                        .andExpect(jsonPath("$.name").value("John"))
                }
            }
        }
    }
}
```

## Fixture Pattern

### Java

```java
public class UserTestFixture {
    public static User createUser(Long id, String email) {
        return User.builder()
            .id(id)
            .name("Test User")
            .email(email)
            .status(UserStatus.ACTIVE)
            .build();
    }

    public static User createUser() {
        return createUser(1L, "test@example.com");
    }

    public static CreateUserRequest createRequest() {
        return new CreateUserRequest("Test User", "test@example.com");
    }
}
```

### Kotlin

```kotlin
object UserTestFixture {
    fun createUser(
        id: Long = 1L,
        email: String = "test@example.com",
        name: String = "Test User",
        status: UserStatus = UserStatus.ACTIVE
    ) = User(
        id = id,
        name = name,
        email = email,
        status = status
    )

    fun createRequest(
        name: String = "Test User",
        email: String = "test@example.com"
    ) = CreateUserRequest(name = name, email = email)

    fun createUsers(count: Int = 3) =
        (1..count).map { createUser(id = it.toLong(), email = "user$it@example.com") }
}
```

## MockK Usage Guide

```kotlin
// Create mock
val repository = mockk<UserRepository>()

// Relaxed mock (returns default values for all methods)
val relaxedMock = mockk<SomeService>(relaxed = true)

// Stubbing
every { repository.findById(1L) } returns Optional.of(user)
every { repository.save(any()) } returns user
every { repository.findById(any()) } returns Optional.empty()

// Stubbing with argument capture
val slot = slot<User>()
every { repository.save(capture(slot)) } answers { slot.captured }

// Verify
verify { repository.save(any()) }
verify(exactly = 1) { repository.findById(1L) }
verify(exactly = 0) { repository.delete(any()) }

// Verify order
verifyOrder {
    repository.findById(1L)
    repository.save(any())
}

// Clear mocks
clearMocks(repository)
```

## Mockito Usage Guide (Java)

```java
// Stubbing
when(repository.findById(1L)).thenReturn(Optional.of(user));
when(repository.save(any())).thenReturn(user);

// Verify
verify(repository).save(any());
verify(repository, times(1)).findById(1L);
verify(repository, never()).delete(any());

// Argument capture
ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
verify(repository).save(captor.capture());
User captured = captor.getValue();

// BDD style
given(repository.findById(1L)).willReturn(Optional.of(user));
then(repository).should().save(any());
```

## Test Commands

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "UserGetServiceTest"

# Run tests matching pattern
./gradlew test --tests "*ServiceTest"

# Run with verbose output
./gradlew test --info
```

## Checklist

- [ ] Success case test
- [ ] Failure/exception case test
- [ ] Edge case test (empty list, null, max value)
- [ ] Mock verification (verify)
- [ ] Fixture reuse
- [ ] Run tests to confirm passing
