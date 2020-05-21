package com.programmersbox.testingplaygroundapp

@DslMarker
annotation class ResourceBuilderMarker

class ResourceMaker private constructor(private val keyValues: Map<String, String>) {

    private val stringsConvert: (Map.Entry<String, String>) -> String = { "<string name=\"${it.key}\">${it.value.replace("\n", "\\n")}</string>" }
    private val paramConvert: (String) -> List<Pair<String, String>> = {
        it.split(" ").filter { it == "%d" || it == "%s" }.mapIndexed { i, s ->
            val idName = "${if (s == "%d") "int" else "string"}$i"
            Pair("$idName: ${if (s == "%d") "Int" else "String"}", idName)
        }
    }
    private val kotlinConverting: (Map.Entry<String, String>) -> String = {
        val value = paramConvert(it.value)
        val secondParams = if (value.isNotEmpty()) value.joinToString(", ", prefix = ", ") { it.second } else ""
        "override fun ${it.key}(${value.joinToString(", ") { it.first }}) = resources.getString(R.string.${it.key}$secondParams)"
    }
    private val kotlinInterfaceConverting: (Map.Entry<String, String>) -> String = {
        "fun ${it.key}(${paramConvert(it.value).joinToString(", ") { it.first }}): String"
    }

    private val javaParamConvert: (String) -> List<Pair<String, String>> = {
        it.split(" ").filter { it == "%d" || it == "%s" }.mapIndexed { i, s ->
            val idName = "${if (s == "%d") "int" else "string"}$i"
            Pair("${if (s == "%d") "int" else "String"} $idName", idName)
        }
    }

    private val javaConverting: (Map.Entry<String, String>) -> String = {
        val value = javaParamConvert(it.value)
        val secondParams = if (value.isNotEmpty()) value.joinToString(", ", prefix = ", ") { it.second } else ""
        "@Override\npublic String ${it.key}(${value.joinToString(", ") { it.first }}) {\n\treturn resources.getString(R.string.${it.key}$secondParams);\n}"
    }

    private val javaInterfaceConverting: (Map.Entry<String, String>) -> String = {
        "public String ${it.key}(${javaParamConvert(it.value).joinToString(", ") { it.first }});"
    }

    fun toStringsXML() = keyValues.map(stringsConvert)
    fun toKotlinOverrideClass() = keyValues.map(kotlinConverting)
    fun toKotlinInterface() = keyValues.map(kotlinInterfaceConverting)
    fun toJavaOverrideClass() = keyValues.map(javaConverting)
    fun toJavaInterface() = keyValues.map(javaInterfaceConverting)

    class Builder {
        private val keyValues = mutableMapOf<String, String>()

        @ResourceBuilderMarker
        fun addItems(vararg pairs: Pair<String, String>) = apply { keyValues.putAll(pairs) }

        fun build() = ResourceMaker(keyValues)

        companion object {
            @ResourceBuilderMarker
            operator fun invoke(block: Builder.() -> Unit) = Builder().apply(block).build()
        }
    }

}