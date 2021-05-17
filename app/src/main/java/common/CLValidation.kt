package common

object CLValidation {
    //the email should be in the form "personname<@>maildomain<.>extention
    private const val EMAIL_REGEX = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"

    //password must contain length 6, at-least one Caps letter, one special character and one numeric digit
    private const val PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=]).{4,}$"

    fun isEmailValid(email: String?): Boolean =
        (!email.isNullOrEmpty() && EMAIL_REGEX.toRegex().matches(email))

    fun isPasswordValid(password: String?): Boolean =
        (!password.isNullOrEmpty() && PASSWORD_REGEX.toRegex().matches(password))
}