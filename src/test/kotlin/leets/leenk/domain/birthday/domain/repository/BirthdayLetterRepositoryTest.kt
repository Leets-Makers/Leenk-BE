package leets.leenk.domain.birthday.domain.repository

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringTestExtension
import io.kotest.extensions.spring.SpringTestLifecycleMode
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManager
import leets.leenk.config.MysqlTestConfig
import leets.leenk.domain.birthday.domain.entity.BirthdayLetter
import leets.leenk.domain.user.domain.entity.User
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@DataJpaTest
@ActiveProfiles("test")
@Import(MysqlTestConfig::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BirthdayLetterRepositoryTest(
    private val em: EntityManager,
    private val birthdayLetterRepository: BirthdayLetterRepository,
) : BehaviorSpec() {
    override fun extensions() = listOf(SpringTestExtension(SpringTestLifecycleMode.Root))

    init {
        Given("특정 수신자에게 여러 편지가 있을 때") {
            val sender = persistUser("sender")
            val receiver1 = persistUser("receiver1")
            val receiver2 = persistUser("receiver2")

            val letter1 = birthdayLetterRepository.save(createLetter(sender, receiver1, "첫 번째 편지"))
            val letter2 = birthdayLetterRepository.save(createLetter(sender, receiver1, "두 번째 편지"))
            val letter3 = birthdayLetterRepository.save(createLetter(sender, receiver1, "세 번째 편지"))
            val otherLetter = birthdayLetterRepository.save(createLetter(sender, receiver2, "다른 수신자"))

            flushAndClear()

            updateLetterCreateDate(letter1.id!!, BASE_TIME.plusMinutes(1))
            updateLetterCreateDate(letter2.id!!, BASE_TIME.plusMinutes(2))
            updateLetterCreateDate(letter3.id!!, BASE_TIME.plusMinutes(3))
            updateLetterCreateDate(otherLetter.id!!, BASE_TIME.plusMinutes(4))

            flushAndClear()

            When("수신자 ID로 편지를 조회하면") {
                val result = birthdayLetterRepository.findAllByReceiverIdOrderByCreateDateDesc(receiver1.id!!)

                Then("해당 수신자의 편지만 생성일 역순으로 조회되어야 한다") {
                    result shouldHaveSize 3
                    result.map { it.id } shouldContainExactly listOf(letter3.id, letter2.id, letter1.id)
                    result.forEach { letter ->
                        letter.receiver.id shouldBe receiver1.id
                    }
                }
            }
        }

        Given("편지가 없을 때") {
            val receiver = persistUser("receiver")

            When("수신자 ID로 편지를 조회하면") {
                val result = birthdayLetterRepository.findAllByReceiverIdOrderByCreateDateDesc(receiver.id!!)

                Then("빈 리스트를 반환해야 한다") {
                    result shouldHaveSize 0
                }
            }
        }

        Given("특정 기간 내에 여러 편지가 있을 때") {
            val sender = persistUser("sender")
            val receiver = persistUser("receiver")

            val letter1 = birthdayLetterRepository.save(createLetter(sender, receiver, "기간 내 1"))
            val letter2 = birthdayLetterRepository.save(createLetter(sender, receiver, "기간 내 2"))
            val letter3 = birthdayLetterRepository.save(createLetter(sender, receiver, "기간 밖"))

            flushAndClear()

            val start = BASE_TIME
            val end = BASE_TIME.plusHours(2)

            updateLetterCreateDate(letter1.id!!, start.plusMinutes(10))
            updateLetterCreateDate(letter2.id!!, start.plusMinutes(20))
            updateLetterCreateDate(letter3.id!!, end.plusMinutes(10)) // 기간 밖

            flushAndClear()

            When("기간 내 편지 개수를 세면") {
                val count =
                    birthdayLetterRepository.countByReceiverIdAndCreateDateBetween(
                        receiver.id!!,
                        start,
                        end,
                    )

                Then("기간 내 편지 개수만 반환되어야 한다") {
                    count shouldBe 2
                }
            }
        }

        Given("편지가 하나도 없을 때") {
            val receiver = persistUser("receiver")

            When("기간 내 편지 개수를 세면") {
                val count =
                    birthdayLetterRepository.countByReceiverIdAndCreateDateBetween(
                        receiver.id!!,
                        BASE_TIME,
                        BASE_TIME.plusDays(1),
                    )

                Then("0을 반환해야 한다") {
                    count shouldBe 0
                }
            }
        }

        Given("lastReadAt이 null이고 기간 내 편지가 있을 때") {
            val sender = persistUser("sender")
            val receiver = persistUser("receiver")

            val letter = birthdayLetterRepository.save(createLetter(sender, receiver, "새 편지"))

            flushAndClear()

            val start = BASE_TIME
            val end = BASE_TIME.plusDays(1)

            updateLetterCreateDate(letter.id!!, start.plusMinutes(10))

            flushAndClear()

            When("새로운 편지가 있는지 확인하면") {
                val hasNew =
                    birthdayLetterRepository.checkNewBirthdayLetter(
                        receiver.id!!,
                        start,
                        end,
                        null,
                    )

                Then("true를 반환해야 한다") {
                    hasNew shouldBe true
                }
            }
        }

        Given("lastReadAt 이후 새 편지가 있을 때") {
            val sender = persistUser("sender")
            val receiver = persistUser("receiver")

            val oldLetter = birthdayLetterRepository.save(createLetter(sender, receiver, "읽은 편지"))
            val newLetter = birthdayLetterRepository.save(createLetter(sender, receiver, "새 편지"))

            flushAndClear()

            val start = BASE_TIME
            val end = BASE_TIME.plusDays(1)
            val lastReadAt = BASE_TIME.plusMinutes(15)

            updateLetterCreateDate(oldLetter.id!!, start.plusMinutes(10))
            updateLetterCreateDate(newLetter.id!!, start.plusMinutes(20))

            flushAndClear()

            When("새로운 편지가 있는지 확인하면") {
                val hasNew =
                    birthdayLetterRepository.checkNewBirthdayLetter(
                        receiver.id!!,
                        start,
                        end,
                        lastReadAt,
                    )

                Then("true를 반환해야 한다") {
                    hasNew shouldBe true
                }
            }
        }

        Given("lastReadAt 이후 새 편지가 없을 때") {
            val sender = persistUser("sender")
            val receiver = persistUser("receiver")

            val letter = birthdayLetterRepository.save(createLetter(sender, receiver, "읽은 편지"))

            flushAndClear()

            val start = BASE_TIME
            val end = BASE_TIME.plusDays(1)
            val lastReadAt = BASE_TIME.plusMinutes(20)

            updateLetterCreateDate(letter.id!!, start.plusMinutes(10))

            flushAndClear()

            When("새로운 편지가 있는지 확인하면") {
                val hasNew =
                    birthdayLetterRepository.checkNewBirthdayLetter(
                        receiver.id!!,
                        start,
                        end,
                        lastReadAt,
                    )

                Then("false를 반환해야 한다") {
                    hasNew shouldBe false
                }
            }
        }

        Given("기간 밖의 편지만 있을 때") {
            val sender = persistUser("sender")
            val receiver = persistUser("receiver")

            val letter = birthdayLetterRepository.save(createLetter(sender, receiver, "기간 밖 편지"))

            flushAndClear()

            val start = BASE_TIME
            val end = BASE_TIME.plusDays(1)

            updateLetterCreateDate(letter.id!!, end.plusMinutes(10))

            flushAndClear()

            When("새로운 편지가 있는지 확인하면") {
                val hasNew =
                    birthdayLetterRepository.checkNewBirthdayLetter(
                        receiver.id!!,
                        start,
                        end,
                        null,
                    )

                Then("false를 반환해야 한다") {
                    hasNew shouldBe false
                }
            }
        }
    }

    private fun createLetter(
        sender: User,
        receiver: User,
        message: String,
    ): BirthdayLetter =
        BirthdayLetter(
            sender = sender,
            receiver = receiver,
            message = message,
        )

    private fun updateLetterCreateDate(
        letterId: Long,
        createDate: LocalDateTime,
    ) {
        em
            .createQuery("UPDATE BirthdayLetter b SET b.createDate = :createDate WHERE b.id = :id")
            .setParameter("createDate", createDate)
            .setParameter("id", letterId)
            .executeUpdate()
    }

    private fun persistUser(name: String): User {
        val user =
            User
                .builder()
                .name(name)
                .birthday(java.time.LocalDate.of(2000, 1, 1))
                .cardinal(1)
                .totalReactionCount(0)
                .termsAgreement(true)
                .privacyAgreement(true)
                .build()
        em.persist(user)
        return user
    }

    private fun flushAndClear() {
        em.flush()
        em.clear()
    }

    companion object {
        private val BASE_TIME = LocalDateTime.of(2025, 12, 25, 0, 0)
    }
}
