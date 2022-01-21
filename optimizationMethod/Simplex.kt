package optimizationMethod

import java.lang.StringBuilder

val separator = "_____________________________________________________________________________________________________________________________________________________________________________"

class Simplex(private var coeffs: ArrayList<Fraction>, A: Matrix, private var sol: Row) {
    private var J_x: IntArray
    private val size = A.dimens
    private var z: Matrix
    private var basis: Matrix

    init{
        J_x = (0 until size.y).filter { sol[it].value() > 0.0 }.toIntArray()
        val t = J_x.map{A[":", it]}.toTypedArray()
        basis = Matrix(*t.map { rowOf(it.copy()) }.toTypedArray()).T
        val temp = ArrayList<ArrayList<Fraction>>()
        for(i in 0 until size.y){
            temp.add(solveLinalg(basis = basis, col = A[":", i]))
        }
        z = Matrix(*(temp.map { rowOf(it.copy()) }.toTypedArray())).T
    }

    fun anySolution(deltas: ArrayList<Fraction>) =
        deltas.all{it.value() <= 0.0}
    fun noSolution(deltas: ArrayList<Fraction>): Boolean {
        for(i in 0 until size.y){
            if((i !in J_x) && (deltas[i].value() > 0.0) && (z[":", i].all{it.value() <= 0}))
                return true
        }
        return false
    }

    fun delta(): Row {
        val ans = ArrayList<Fraction>()
        val c_z = arrayListOf(*J_x.map{coeffs[it]}.toTypedArray())
        repeat(size.y){
            ans.add(c_z * z[":", it] - coeffs[it])
        }
        return rowOf(ans)
    }

    fun thetas(s: Int): Column {
        if(s == -1)
            return columnOf(arrayListOf(*Array(size.x) { Fraction() }))
        val ans = ArrayList<Fraction>()
        for(i in 0 until size.x){
            if(z[i, s].value() <= 0.0)
                ans.add(Fraction(-1))
            else ans.add(sol[J_x[i]] / z[i, s])
        }
        return columnOf(ans)
    }

    fun show(size: dim, cY: Column, solution: Column, res: Fraction, deltas: Row, theta: Column,
                sr: Int, s: Int){
        val (x, y) = size
        val n = x + 1
        val m = y + 4
        val temp = Matrix(*Array(x + 1)
            { rowOf(arrayListOf(*(Array(y + 4) {Fraction()}))) })
        temp[":", ":", 0, 3] = z
        temp[":", 0] = cY
        temp[":", 1] = Column(*J_x.map{Fraction(it.toLong()) + 1.F}.toTypedArray())
        temp[":", 2] = solution
        temp[x, 2] = res
        temp[x, ":", 3] = deltas
        temp[":", y + 3] = theta

        println(separator.slice(0..m * 10 + 3))

        var tline = "coeffs:"
        for(i in coeffs){
            tline += "%10s".format(i)
        }
        println("%${(m - 1) * 10 + 3}s".format(tline))
        tline = "%10s%10s%10s".format("Coeffs", "Basis", "Answer ")
        for(i in 1..y){
            tline += "%10s".format("A${i}")
        }
        println("%${(m - 1) * 10 + 3}s".format(tline))

        val sb = StringBuilder()

        for(i in 0 until n){
            var line = ""
            if(i == x)
                line += "\n"
            for(j in 0 until m){
                if (j == 3)
                    line += " | "
                if(i == sr && j == s + 3)
                    line += "%10s".format("[${temp[i][j].toString()}]")
                else{
                    if (i < x && j == m - 1) {
                        if (temp[i][j].value() <= 0)
                            line += "%10s".format("inf")
                        else line += "%10s".format(temp[i][j].toString())
                    } else line += "%10s".format(temp[i][j].toString())
                }
            }
            line += "\n"
            sb.append(line)
        }
        println(sb.toString().dropLast(1))
        println(separator.slice(0..m * 10 + 3))
        println()
    }

    fun process() {
        while(true) {
            val c_t = Column(*(J_x.map{coeffs[it]}.toTypedArray()))
            val t_sol = Column(*(J_x.map{sol[it]}.toTypedArray()))
            val result = c_t * t_sol
            val deltas = delta()
            val s = argmax(deltas)
            val theta = thetas(s)
            val sr = argmin(theta)
            show(size, c_t, t_sol, result, deltas, theta, sr, s)
            if(anySolution(deltas)) {
                println("Solution: \n${sol}")
                println("min(f) = $result")
                break
            }
            if(noSolution(deltas)) {
                println("No solution")
                break
            }
            val r = J_x[sr]
            sol[s] = sol[r] / z[sr, s]
            z[sr] = z[sr] / z[sr, s]
            for(i in 0 until size.x){
                if(i != sr){
                    sol[J_x[i]].minusAssign(sol[s] * z[i, s])
                    z[i].minusFrom(z[sr] * z[i, s])
                }
            }
            sol[r] = Fraction()
            J_x[sr] = s
        }
    }

    private fun argmax(deltas: ArrayList<Fraction>): Int {
        var res = -1
        var t = 0.0
        for(i in deltas.indices){
            if(deltas[i].value() > t) {
                res = i
                t = deltas[i].value()
            }
        }
        return res
    }

    private fun argmin(theta: ArrayList<Fraction>): Int {
        var res = -1
        var t = Double.MAX_VALUE
        for(i in theta.indices){
            if(theta[i].value() < t && theta[i].value() > 0.0) {
                res = i
                t = theta[i].value()
            }
        }
        return res
    }
}

fun main(){
    val A = Matrix(mk( 1, -1, 4, 0, -2, -1, 0),
        mk(3, 2, -1, 1, 0, 0,  1),
        mk(5, 3, 1, 2, -1, 0, 0))
    val coeffs = mk(-2, -1, 3, 1, -4, 0, 0)
    val sol = mk(0, 2, 0, 20, 0, 2, 0)
/*    val A = Matrix(mk( -1, 2, 2, 0, 1, 0),
        mk(1, 1, 1, 0, 0, 1),
        mk(2, 0, 2, 1, 0, 0))
    val coeffs = mk(-2, 2, 1, 3, 0, 1)
    val sol = mk(0, 0, 0, 7, 4, 5)*/
    val process = Simplex(coeffs = coeffs, A = A, sol = sol)
    process.process()
}