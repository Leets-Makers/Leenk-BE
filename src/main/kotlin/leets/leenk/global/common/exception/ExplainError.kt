package leets.leenk.global.common.exception

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class ExplainError(
    val value: String = "",
)
