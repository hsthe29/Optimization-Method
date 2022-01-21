package optimizationMethod

import java.lang.IndexOutOfBoundsException
import java.lang.StringBuilder

data class dim(val x: Int, val y: Int){
    override fun toString(): String {
        return "[$x, $y]"
    }
}

class Matrix(vararg arrays: Row): ArrayList<ArrayList<Fraction>>() {
    init {
        addAll(arrays)
    }

    var dimens = dim(this.size, this[0].size)

    override fun toString(): String {
        var blank = 0
        for(i in 0 until dimens.x){
            for(j in 0 until dimens.y){
                blank = blank.coerceAtLeast(this[i][j].toString().length)
            }
        }
        val s = StringBuilder()
        for(i in 0 until dimens.x){
            var line = " ["
            for(j in 0 until dimens.y){
                line += "%${blank + 1}s,".format(this[i][j].toString())
            }
            line = line.dropLast(1)
            line += "]\n"
            s.append(line)
        }
        var res = s.toString().drop(1)
        res = res.dropLast(1)
        return "[${res}]"
    }

    operator fun get(s1: String, s2: String): Matrix {
        if(s1 != ":" || s2 != ":")
            throw IndexOutOfBoundsException("Invalid indexes")
        return Matrix(*this.map { rowOf(it.copy()) }.toTypedArray())
    }

    operator fun get(s1: String, y: Int): Column {
        if(s1 != ":")
            throw IndexOutOfBoundsException("Invalid indexes")
        return Column(*(this.map { it[y].clone() }.toTypedArray()))
    }

    operator fun get(x: Int, s2: String): Row {
        if(s2 != ":")
            throw IndexOutOfBoundsException("Invalid indexes")
        return Row(*this[x].copy().toTypedArray())
    }

    operator fun get(x: Int, y: Int): Fraction {
        if(x < 0 || y < 0 || x >= dimens.x || y >= dimens.y)
            throw IndexOutOfBoundsException("Index out of range $x, $y")
        return this[x][y].clone()
    }
    // set

    operator fun set(s1: String, s2: String, h: Int = 0, w: Int = 0, value: ArrayList<ArrayList<Fraction>>) {
        if(s1 != ":" || s2 != ":")
            throw IndexOutOfBoundsException("Invalid indexes")
        val endH = value.size + h
        val endW = value[0].size + w
        for(i in h until endH){
            for(j in w until endW){
                this[i][j] = value[i - h][j - w]
            }
        }
    }

    operator fun set(s1: String, s2: String, h: Int = 0, w: Int = 0, value: Matrix) {
        if(s1 != ":" || s2 != ":")
            throw IndexOutOfBoundsException("Invalid indexes")
        val endH = value.dimens.x + h
        val endW = value.dimens.y + w
        for(i in h until endH){
            for(j in w until endW){
                this[i][j] = value[i - h][j - w]
            }
        }
    }

    operator fun set(s1: String, y: Int, h: Int = 0, value: Column) {
        if(s1 != ":")
            throw IndexOutOfBoundsException("Invalid indexes")
        val endH = value.size + h
        for(i in h until endH)
            this[i][y] = value[i - h].clone()
    }

    operator fun set(x: Int, s2: String, w: Int = 0, value: Row) {
        if(s2 != ":")
            throw IndexOutOfBoundsException("Invalid indexes")
        val endW = value.size + w
        for(j in w until endW)
            this[x][j] = value[j - w].clone()
    }

    operator fun set(x: Int, y: Int, value: Fraction) {
        if(x < 0 || y < 0 || x >= dimens.x || y >= dimens.y)
            throw IndexOutOfBoundsException("Index out of range")
        this[x][y] = value.clone()
    }

    val T: Matrix
        get(): Matrix {
            val res = Array(dimens.y){ rowOf(arrayListOf(*Array(dimens.x) { Fraction() })) }
            for(i in 0 until dimens.x){
                for(j in 0 until dimens.y){
                    res[j][i] = this[i][j]
                }
            }
            return Matrix(*res)
        }
}

class Column(vararg elements: Number): ArrayList<Fraction>() {
    init {
        for(i in elements)
            add(Fraction(i.toDouble()))
    }

    val T: Row
        get() = rowOf(this)

    constructor(vararg elements: Fraction) : this(0) {
        this.clear()
        for(i in elements)
            add(i)
    }

    override fun toString(): String {
        var blank = 0
        for(i in this){
            blank = blank.coerceAtLeast(i.toString().length)
        }
        var line = ""
        for(i in this){
            line += "%${blank + 1}s,\n".format(i.toString())
        }
        line = line.dropLast(2)
        line = line.drop(1)

        return "[${line}]"
    }
}

class Row(vararg elements: Number): ArrayList<Fraction>() {
    init {
        for(i in elements)
            add(Fraction(i.toDouble()))
    }

    val T: Column
        get() = columnOf(this)

    constructor(vararg elements: Fraction) : this(0) {
        this.clear()
        for(i in elements)
            add(i)
    }

    override fun toString(): String {
        var blank = 0
        for(i in this){
            blank = blank.coerceAtLeast(i.toString().length)
        }
        var line = ""
        for(i in this){
            line += "%${blank + 1}s,".format(i.toString())
        }
        line = line.dropLast(1)
        line = line.drop(1)

        return "[${line}]"
    }
}
