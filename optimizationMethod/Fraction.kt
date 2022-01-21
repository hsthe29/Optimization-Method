package optimizationMethod

import kotlin.math.abs

val Int.F: Fraction
    get() = Fraction(this.toLong())
val Long.F: Fraction
    get() = Fraction(this)
val Double.F: Fraction
    get() = Fraction(this)

class Fraction(a : Long = 0, b : Long = 1) {

    var numerator: Long = 0L
    var denominator: Long = 1L

    init {
        val gcd = gcd(a, b)
        numerator = a / gcd
        denominator = b / gcd
        simplify()
    }

    constructor(a: Double) : this() {
        var s = a.toString()
        val index = s.indexOfFirst { it == '.' }
        s = s.replace(".", "")
        val _a = s.toLong()
        val b = 10L pow (s.length - index).toLong()
        val gcd = gcd(_a, b)
        numerator = _a / gcd
        denominator = b / gcd
    }

    private fun simplify() {
        val gcd = gcd(numerator, denominator)
        numerator /= gcd
        denominator /= gcd
        if(numerator.toDouble() / denominator >= 0.0) {
            numerator = abs(numerator)
            denominator = abs(denominator)
        } else {
            if(denominator < 0) {
                denominator = -denominator
                numerator = -numerator
            }
        }
    }

    operator fun plus(other : Fraction) : Fraction {
        val t = lcm(denominator, other.denominator)
        val t1 = t / denominator
        val t2 = t / other.denominator
        return Fraction(numerator * t1 + other.numerator * t2, t)
    }
    operator fun plusAssign(other: Fraction) {
        val t = lcm(denominator, other.denominator)
        val t1 = t / denominator
        val t2 = t / other.denominator
        denominator = t
        numerator = numerator * t1 + other.numerator * t2
        simplify()
    }

    operator fun minus(other : Fraction): Fraction {
        val t = lcm(denominator, other.denominator)
        val t1 = t / denominator
        val t2 = t / other.denominator
        return Fraction(numerator * t1 - other.numerator * t2, t)
    }
    operator fun minusAssign(other: Fraction) {
        val t = lcm(denominator, other.denominator)
        val t1 = t / denominator
        val t2 = t / other.denominator
        denominator = t
        numerator = numerator * t1 - other.numerator * t2
        simplify()
    }

    operator fun times(other : Fraction) =
        Fraction(numerator * other.numerator, denominator * other.denominator)

    operator fun timesAssign(other: Fraction) {
        numerator *= other.numerator
        denominator *= other.denominator
        simplify()
    }

    operator fun div(other : Fraction) =
        Fraction(numerator * other.denominator, denominator * other.numerator)

    operator fun divAssign(other: Fraction) {
        numerator *= other.denominator
        denominator *= other.numerator
        simplify()
    }

    override operator fun equals(other: Any?) =
        if(other is Fraction) value() == other.value()
        else false

    operator fun compareTo(other: Fraction) : Int {
        return value().compareTo(other.value())
    }

    fun value() = numerator.toDouble() / denominator

    override fun toString(): String {
        if(numerator == 0L)
            return "0"
        if(denominator == 1L)
            return "$numerator"
        return "$numerator/$denominator"
    }

    fun clone() = Fraction(this.numerator, this.denominator)
}