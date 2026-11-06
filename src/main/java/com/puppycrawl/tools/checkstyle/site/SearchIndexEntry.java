///////////////////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code and other text files for adherence to a set of rules.
// Copyright (C) 2001-2026 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
///////////////////////////////////////////////////////////////////////////////////////////////

package com.puppycrawl.tools.checkstyle.site;

import javax.annotation.Nullable;

/**
 * Represents a single entry in the client-side search index.
 *
 * @param location where the entry points to (title and url)
 * @param classification category, type and ranking weight
 * @param content description, keywords and since version
 */
// Code Smell Refactor: 8 parameters grouped into 3 parameter objects
public record SearchIndexEntry(EntryLocation location,
                               EntryClassification classification,
                               EntryContent content) {
    /**
     * Where the entry points to.
     *
     * @param title display name
     * @param url relative URL
     */
    public record EntryLocation(String title, String url) {
    }

     /**
     * How the entry is categorised and ranked.
     *
     * @param category category label
     * @param type document type
     * @param weight ranking weight for search results
     */
    public record EntryClassification(String category, String type, int weight) {
    }

    /**
     * The searchable text of the entry.
     *
     * @param description short description
     * @param keywords comma-separated keywords
     * @param since the version this module/property was introduced (can be empty)
     */
    public record EntryContent(String description, String keywords, String since) {
    }
    /** String literal for comma. */
    private static final String COMMA = ",";

    /** Maximum ASCII value for the lookup table. */
    private static final int ASCII_MAX = 128;

    /** Lookup table for JSON escape sequences for characters {@literal <} 128 (ASCII range). */
    private static final String[] JSON_ESCAPES;

    static {
        JSON_ESCAPES = new String[ASCII_MAX];
        JSON_ESCAPES['\\'] = "\\\\";
        JSON_ESCAPES['\"'] = "\\\"";
        JSON_ESCAPES['\n'] = "\\n";
        JSON_ESCAPES['\r'] = "\\r";
        JSON_ESCAPES['\t'] = "\\t";
    }

    /**
     * Serialises this entry to a compact JSON object string.
     *
     * @return JSON object string
     */
    public String toJson() {
         return "{"
            + "\"title\":" + jsonString(location.title()) + COMMA
            + "\"url\":" + jsonString(location.url()) + COMMA
            + "\"category\":" + jsonString(classification.category()) + COMMA
            + "\"type\":" + jsonString(classification.type()) + COMMA
            + "\"description\":" + jsonString(content.description()) + COMMA
            + "\"since\":" + jsonString(content.since()) + COMMA
            + "\"weight\":" + classification.weight() + COMMA
            + "\"keywords\":" + jsonString(content.keywords())
            + "}";
    }

    /**
     * Wraps a string value in JSON double quotes and escapes special characters.
     *
     * @param value the raw string to encode
     * @return JSON-safe quoted string, or {@code ""} if value is null
     */
    private static String jsonString(@Nullable String value) {
        String result = "\"\"";
        if (value != null) {
            final int length = value.length();
            final StringBuilder sb = new StringBuilder(length + 2);
            sb.append('\"');
            for (int index = 0; index < length; index++) {
                final char ch = value.charAt(index);
                final String escape;
                if (ch < ASCII_MAX) {
                    escape = JSON_ESCAPES[ch];
                }
                else {
                    escape = null;
                }
                if (escape == null) {
                    sb.append(ch);
                }
                else {
                    sb.append(escape);
                }
            }
            sb.append('\"');
            result = sb.toString();
        }
        return result;
    }

}
