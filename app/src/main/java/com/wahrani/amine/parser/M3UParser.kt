package com.wahrani.amine.parser

import com.wahrani.amine.model.Channel

object M3UParser {

    fun parse(content: String): List<Channel> {
        val channels = mutableListOf<Channel>()
        val lines = content.lines()
        var i = 0

        while (i < lines.size) {
            val line = lines[i].trim()
            if (line.startsWith("#EXTINF:")) {
                val name = extractName(line)
                val logoUrl = extractAttribute(line, "tvg-logo")
                val groupTitle = extractAttribute(line, "group-title")
                val tvgId = extractAttribute(line, "tvg-id")
                val sourceLabel = extractSourceLabel(name)

                // Next non-empty line should be the URL
                i++
                while (i < lines.size && lines[i].trim().isEmpty()) {
                    i++
                }
                if (i < lines.size && !lines[i].trim().startsWith("#")) {
                    val url = lines[i].trim()
                    if (url.startsWith("http://") || url.startsWith("https://")) {
                        channels.add(
                            Channel(
                                name = name,
                                url = url,
                                logoUrl = logoUrl,
                                groupTitle = groupTitle.ifEmpty { "Uncategorized" },
                                tvgId = tvgId,
                                sourceLabel = sourceLabel
                            )
                        )
                    }
                }
            }
            i++
        }

        return channels
    }

    private fun extractName(line: String): String {
        val commaIndex = line.lastIndexOf(',')
        return if (commaIndex >= 0 && commaIndex + 1 < line.length) {
            line.substring(commaIndex + 1).trim()
        } else {
            "Unknown"
        }
    }

    private fun extractAttribute(line: String, attr: String): String {
        val pattern = """$attr="([^"]*)"""".toRegex()
        return pattern.find(line)?.groupValues?.getOrElse(1) { "" } ?: ""
    }

    private fun extractSourceLabel(name: String): String {
        val bracketPattern = """\[([^\]]+)\]""".toRegex()
        return bracketPattern.find(name)?.groupValues?.getOrElse(1) { "" } ?: ""
    }
}
