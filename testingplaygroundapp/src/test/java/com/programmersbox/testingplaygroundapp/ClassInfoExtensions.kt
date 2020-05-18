package com.programmersbox.testingplaygroundapp


import com.programmersbox.loggingutils.FrameType
import com.programmersbox.loggingutils.Loged
import com.programmersbox.loggingutils.f
import com.programmersbox.loggingutils.frame
import kotlin.reflect.KClass

@Suppress("TestFunctionName")
fun ClassInfo(vararg classes: Class<*>) = classes.map { ClassInfo(it) }

fun Class<*>.toClassInfo() = ClassInfo(this)
fun KClass<*>.toClassInfo() = ClassInfo(this.java)

enum class ClassItemEnums {
    CONSTRUCTOR,
    PROPERTY,
    FUNCTION
}

enum class ClassInfoEnums {
    SUPERCLASS,
    SUBCLASS,
}

enum class ClassNameEnum {
    CLASS_NAME
}

enum class ClassEnumEnum {
    ENUM
}

operator fun ClassInfo.get(type: ClassItemEnums): List<ClassInfo.ClassInfoItems> = when (type) {
    ClassItemEnums.CONSTRUCTOR -> constructors
    ClassItemEnums.PROPERTY -> propertyList
    ClassItemEnums.FUNCTION -> methodList
}

operator fun ClassInfo.get(type: ClassInfoEnums): List<ClassInfo> = when (type) {
    ClassInfoEnums.SUPERCLASS -> superClasses
    ClassInfoEnums.SUBCLASS -> subClasses
}

operator fun ClassInfo.get(type: ClassNameEnum) = type.let { className }
operator fun ClassInfo.get(type: ClassEnumEnum) = type.let { enumSet }

//--------------------------------------------------------------------------------------------------------------------------------------------------\\

fun <T : Comparable<T>> ClassInfo.getClassBox(frame: FrameType = FrameType.BOX, sort: ((ClassInfo.ClassInfoItems) -> T?)? = null): String {
    val tag = "$className${if (superClasses.isNotEmpty()) superClasses.joinToString(prefix = ": ") { it.className } else ""}"
    val prop = "Properties"
    val method = "Methods"
    val inn = "Inner Classes"
    val con = "Constructors"
    val enum = "Enums"
    fun List<*>.length(): Int = maxBy { it.toString().length }?.toString()?.length ?: 0
    val size = listOf(
        tag.length,
        con.length, constructors.length(),
        enum.length, enumSet.length(),
        prop.length, propertyList.length(),
        method.length, methodList.length(),
        inn.length, subClasses.length()
    ).max()!!
    val section: (List<*>, String) -> String? = { list, header -> if (list.isNotEmpty()) "$header${"-".repeat(size - header.length)}" else null }
    return listOfNotNull(
        section(constructors, con), *constructors.let { sort.sortedBy(it) }.toTypedArray(),
        section(enumSet, enum), *enumSet.toTypedArray(),
        section(propertyList, prop), *propertyList.let { sort.sortedBy(it) }.toTypedArray(),
        section(methodList, method), *methodList.let { sort.sortedBy(it) }.toTypedArray(),
        section(subClasses, inn), *subClasses.map { it.className }.toTypedArray()
    ).frame(frame.copy(top = tag))
}

private fun <T, R : Comparable<R>> ((T) -> R?)?.sortedBy(list: Collection<T>) = this?.let { list.sortedBy(it) } ?: list

fun <T : Comparable<T>> ClassInfo.printClassInfoInBox(sort: ((ClassInfo.ClassInfoItems) -> T?)? = null) =
    Loged.f(listOf(this, *subClasses.union(getSubClasses(subClasses)).toTypedArray()).joinToString("\n") { it.getClassBox(sort = sort) })

fun <T : Comparable<T>, R : Comparable<R>> ClassInfo.printClassInfoInBox(
    classSort: ((ClassInfo) -> R?)? = null, sort: ((ClassInfo.ClassInfoItems) -> T?)? = null
) = Loged.f(
    listOf(this, *subClasses.union(getSubClasses(subClasses)).toTypedArray())
        .let { classSort.sortedBy(it) }.joinToString("\n") { it.getClassBox(sort = sort) }
)

fun getSubClasses(sub: List<ClassInfo>): List<ClassInfo> = when {
    sub.isEmpty() -> emptyList()
    sub.size == 1 -> sub
    else -> sub.flatMap { getSubClasses(it.subClasses) }
}

//--------------------------------------------------------------------------------------------------------------------------------------------------\\

fun String.repeating(n: Int, block: (String) -> String): String {
    var newString = this
    repeat((0 until if (n < 2) 0 else n).count()) { newString = block(newString) }
    return newString
}

fun <T : Comparable<T>> ClassInfo.printClassInfoInBoxes(
    n: Int = 0, frame: FrameType = FrameType.BOX, sort: ((ClassInfo.ClassInfoItems) -> T?)? = null
) = Loged.f(listOf(this, *(subClasses + getSubClasses2(subClasses)).toTypedArray()).joinToString("\n") { it.getClassBox(frame, sort) }
    .repeating(n - 2) { it.frame(frame) }, frameType = frame)

fun <T : Comparable<T>> ClassInfo.printClassInfoInBox2(sort: ((ClassInfo.ClassInfoItems) -> T?)? = null) =
    Loged.f(listOf(this, *(subClasses + getSubClasses2(subClasses)).toTypedArray()).joinToString("\n") { it.getClassBox(sort = sort) })

fun getSubClasses2(sub: List<ClassInfo>): List<ClassInfo> = when {
    sub.isEmpty() -> emptyList()
    sub.any { getSubClasses2(it.subClasses).isNotEmpty() } -> sub.filter { it.subClasses.isNotEmpty() }.flatMap { getSubClasses2(it.subClasses) }
    sub.size == 1 -> sub
    else -> sub.flatMap { getSubClasses(it.subClasses) }
}

fun <T : Comparable<T>> ClassInfo.printFullClassInfoInBox(sort: ((ClassInfo.ClassInfoItems) -> T?)? = null) = Loged.f(
    listOf(
        *superClasses.toTypedArray(),
        *getSuperClasses(superClasses).toTypedArray(),
        this,
        *subClasses.toTypedArray(),
        *getSubClasses(subClasses).toTypedArray()
    ).joinToString("\n") { it.getClassBox(sort = sort) }
)

fun getSuperClasses(sup: List<ClassInfo>): List<ClassInfo> = when {
    sup.isEmpty() -> emptyList()
    sup.size == 1 -> sup
    else -> sup.map { getSuperClasses(it.superClasses) }.flatten()
}

fun <T : Comparable<T>> ClassInfo.printClassInfo(sort: ((ClassInfo.ClassInfoItems) -> T?)? = null) =
    listOf(this, *subClasses.union(getSubClasses(subClasses)).toTypedArray()).joinToString("\n") { it.getAllInfo(sort).joinToString("\n") }

fun <T : Comparable<T>> ClassInfo.getAllInfo(sort: ((ClassInfo.ClassInfoItems) -> T?)? = null): List<Any> {
    val tag = "$className${if (superClasses.isNotEmpty()) superClasses.joinToString(prefix = ": ") { it.className } else ""}"
    val prop = "Properties"
    val method = "Methods"
    val inn = "Inner Classes"
    val con = "Constructors"
    val enum = "Enums"
    fun List<*>.length(): Int = maxBy { it.toString().length }?.toString()?.length ?: 0
    val size = listOf(
        tag.length,
        con.length, constructors.length(),
        enum.length, enumSet.length(),
        prop.length, propertyList.length(),
        method.length, methodList.length(),
        inn.length, subClasses.length()
    ).max()!!
    val section: (List<*>, String) -> String? = { list, header -> if (list.isNotEmpty()) "$header${"-".repeat(size - header.length)}" else null }
    return listOfNotNull(
        tag,
        section(constructors, con), *constructors.let { list -> sort.sortedBy(list) }.toTypedArray(),
        section(enumSet, enum), *enumSet.toTypedArray(),
        section(propertyList, prop), *propertyList.let { list -> sort.sortedBy(list) }.toTypedArray(),
        section(methodList, method), *methodList.let { list -> sort.sortedBy(list) }.toTypedArray(),
        section(subClasses, inn), *subClasses.map { it.className }.toTypedArray()
    )
}