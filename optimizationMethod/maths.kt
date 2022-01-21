package optimizationMethod

import kotlin.math.pow

fun mk(vararg elements: Number) = rowOf(arrayListOf(*(elements.map { Fraction(it.toDouble()) }.toTypedArray())))

fun rowOf(value: ArrayList<Fraction>) = Row(*value.toTypedArray())

fun columnOf(value: ArrayList<Fraction>) = Column(*value.toTypedArray())


fun array(vararg arrays: Array<Fraction>) = arrayListOf(*arrays)

operator fun ArrayList<Fraction>.times(o: ArrayList<Fraction>): Fraction {
    val n = o.size
    var ans = Fraction()
    repeat(n){
        ans.plusAssign(this[it] * o[it])
    }
    return ans
}

fun ArrayList<Fraction>.copy(): ArrayList<Fraction> {
    val ans = ArrayList<Fraction>()
    for(i in this)
        ans.add(i.clone())
    return ans
}
operator fun ArrayList<Fraction>.div(f: Fraction): ArrayList<Fraction> {
    val ans = ArrayList<Fraction>()
    for(i in this)
        ans.add(i / f)
    return ans
}

operator fun ArrayList<Fraction>.times(f: Fraction): ArrayList<Fraction> {
    val ans = ArrayList<Fraction>()
    for(i in this)
        ans.add(i * f)
    return ans
}
fun ArrayList<Fraction>.minusFrom(f: ArrayList<Fraction>) {
    for(i in this.indices)
        this[i].minusAssign(f[i])
}

fun gcd(_a : Long, _b : Long) : Long {
    var c : Long
    var a = _a
    var b = _b
    while(b != 0L) {
        c = b
        b = a % b
        a = c
    }
    return a
}

fun lcm(_a: Long, _b: Long): Long {
    val product = _a.toDouble() * _b
    return (product / gcd(_a, _b)).toLong()
}

infix fun Long.pow(b: Long): Long {
    val res = this.toDouble().pow(b.toDouble())
    if(res < Long.MAX_VALUE && res > Long.MIN_VALUE)
        return res.toLong()
    else throw ArithmeticException("Number out of range")
}

fun det(arr: Matrix, n: Int): Fraction {
    if(n == 1)
        return arr[0][0]
    if(n == 2)
        return (arr[0][0] * arr[1][1] - arr[0][1] * arr[1][0])
    val s = Fraction()
    var t = 1
    repeat(n) {
        if(arr[0][it].value() != 0.0){
            val ind = it
            val newArr = arr.filterIndexed { index, _ -> index != 0 }
                .map {
                    arrayListOf(*(it.clone() as ArrayList<Fraction>)
                        .filterIndexed { index, _ -> index != ind }
                        .toTypedArray())
                }.toTypedArray()
            s.plusAssign(arr[0][it] * t.F * det(Matrix(*newArr.map { rowOf(it.copy()) }.toTypedArray()), n - 1))
        }
        t *= -1
    }
    return s
}

fun solveLinalg(basis: Matrix, col: Column): ArrayList<Fraction> {
    val n = basis.dimens.x
    val D = det(basis, n)
    val Dchild = ArrayList<Fraction>()

    for(i in 0 until n) {
        val temp = Matrix(*(basis.map{ rowOf(it.copy()) }.toTypedArray()))
        temp[":", i] = col
        Dchild.add(det(temp, n) / D)
    }

    return Dchild

}
