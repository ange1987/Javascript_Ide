/*
 * Copyright (C) 2018 Tran Le Duy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.duy.ide.javaide.diagnostic.parser.aapt;

import com.android.annotations.NonNull;
import com.duy.ide.diagnostic.model.Message;
import com.duy.ide.diagnostic.parser.ParsingFailedException;
import com.duy.ide.diagnostic.util.OutputLineReader;
import com.duy.ide.logging.ILogger;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Error4Parser extends AbstractAaptOutputParser {

    /**
     * First line of dual-line aapt error.
     * <pre>
     * ERROR parsing XML file &lt;path&gt;
     * &lt;error&gt; at line &lt;line&gt;
     * </pre>
     */
    private static final List<Pattern> MSG_PATTERNS =
            ImmutableList.of(Pattern.compile("^Error\\s+parsing\\s+XML\\s+file\\s(.+)$"),
                    Pattern.compile("^(.+)\\s+at\\s+line\\s+(\\d+)$"));

    @Override
    public boolean parse(@NonNull String line, @NonNull OutputLineReader reader, @NonNull List<Message> messages, @NonNull ILogger logger)
            throws ParsingFailedException {
        Matcher m = MSG_PATTERNS.get(0).matcher(line);
        if (!m.matches()) {
            return false;
        }
        String sourcePath = m.group(1);

        m = getNextLineMatcher(reader, MSG_PATTERNS.get(1));
        if (m == null) {
            throw new ParsingFailedException();
        }
        String msgText = m.group(1);
        String lineNumber = m.group(2);

        Message msg = createMessage(Message.Kind.ERROR, msgText, sourcePath,
                lineNumber, "", logger);
        messages.add(msg);
        return true;
    }
}
