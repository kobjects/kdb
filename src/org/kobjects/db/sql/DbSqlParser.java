package org.kobjects.db.sql;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Jörg Pleumann
 * @version 1.0
 */

import java.util.*;
import org.kobjects.db.*;

public class DbSqlParser {

    private static final int IDENT   = 32;

    private static final int BOOLEAN = 33;

    private static final int NUMBER  = 34;

    private static final int STRING  = 35;

    private static final int LPAR    = 36;

    private static final int RPAR    = 37;

    private static final int NULL    = 38;

    private static final int STOP    = 63;

    private DbTable table;

    private char[] buffer;

    private int bufferPos;

    private int tokenPos;

    private int tokenType;

    private String tokenText;

    public DbSqlParser(DbTable table) {
        this.table = table;
    }

    public DbCondition parse(String sql) throws DbException {
        int sqlLen = sql.length();
        buffer = new char[sqlLen + 1];
        sql.getChars(0, sqlLen, buffer, 0);
        buffer[sqlLen] = '\0';
        bufferPos = 0;
        nextToken();
        return parseExpression();
    }

    private long getDateTime(String s) {
        return 0;
    }

    private void nextToken() throws DbException {
        while (buffer[bufferPos] == ' ') bufferPos++;

        tokenPos = bufferPos;
        char c = buffer[bufferPos];

        if (c == '\0') {
            tokenType = STOP;
        }

        else if ((c >= 'A')  && (c <= 'Z') || (c >= 'a') && (c <= 'z')) {
            do {
                c = buffer[++bufferPos];
            }
            while ((c >= 'A')  && (c <= 'Z') || (c >= 'a') && (c <= 'z'));

            tokenText = new String(buffer, tokenPos, bufferPos - tokenPos);

            if ("AND".equals(tokenText)) tokenType = DbCondition.AND;
            else if ("OR".equals(tokenText)) tokenType = DbCondition.OR;
            else if ("NOT".equals(tokenText)) tokenType = DbCondition.NOT;
            else if ("TRUE".equals(tokenText)) tokenType = BOOLEAN;
            else if ("FALSE".equals(tokenText)) tokenType = BOOLEAN;
            else if ("NULL".equals(tokenText)) tokenType = NULL;
            else tokenType = IDENT;
        }

        else if ((c >= '0')  && (c <= '9') || (c == '+') || (c == '-')) {
            do {
                c = buffer[++bufferPos];
            }
            while ((c >= '0')  && (c <= '9'));

            tokenText = new String(buffer, tokenPos, bufferPos - tokenPos);
            tokenType = NUMBER;
        }

        else if (c == '\'') {
            do {
                c = buffer[++bufferPos];
            }
            while ((c != '\'')  && (c != '\0'));
            bufferPos++;

            if (c == '\0') throw new DbException("Non-terminated string literal");

            tokenText = new String(buffer, tokenPos + 1, bufferPos - tokenPos - 2);
            tokenType = STRING;
        }

        else if (c == '(') {
            bufferPos++;
            tokenType = LPAR;
        }

        else if (c == ')') {
            bufferPos++;
            tokenType = RPAR;
        }

        else if (c == '=') {
            bufferPos++;
            tokenType = DbCondition.EQ;
        }

        else if (c == '~') {
            bufferPos++;
            tokenType = DbCondition.EQ_TEXT;
        }

        else if (c == '<') {
            if (buffer[++bufferPos] == '=') {
                bufferPos++;
                tokenType = DbCondition.LE;
            }
            tokenType = DbCondition.LT;
        }

        else if (c == '>') {
            if (buffer[++bufferPos] == '=') {
                bufferPos++;
                tokenType = DbCondition.GE;
            }
            tokenType = DbCondition.GT;
        }

        System.out.println("nextToken(): " + tokenText);
    }

    private DbCondition parseExpression() throws DbException {
        Vector vector = new Vector();

        vector.addElement(parseTerm());

        while (tokenType == DbCondition.OR) {
            nextToken();
            vector.addElement(parseExpression());
        }

        if (tokenType != STOP) {
            throw new DbException("End of input expected (" + tokenPos + ")");
        }

        if (vector.size() != 0) {
            DbCondition[] array = new DbCondition[vector.size()];
            for (int i = 0; i < vector.size(); i++) {
                array[i] = (DbCondition)vector.elementAt(i);
            }

            return new DbCondition(DbCondition.OR, array);
        }
        else {
            return (DbCondition)vector.elementAt(0);
        }
    }

    private DbCondition parseTerm() throws DbException {
        Vector vector = new Vector();

        vector.addElement(parseFactor());

        while (tokenType == DbCondition.AND) {
            nextToken();
            vector.addElement(parseExpression());
        }

        if (vector.size() != 0) {
            DbCondition[] array = new DbCondition[vector.size()];
            for (int i = 0; i < vector.size(); i++) {
                array[i] = (DbCondition)vector.elementAt(i);
            }

            return new DbCondition(DbCondition.AND, array);
        }
        else {
            return (DbCondition)vector.elementAt(0);
        }
    }

    private DbCondition parseFactor() throws DbException {
        if (tokenType == IDENT) {
            int field = table.findField(tokenText);
            if (field == -1) {
                throw new DbException("Unknown field \"" + tokenText + "\"");
            }

            nextToken();

            if ((tokenType < DbCondition.LT) || (tokenType > DbCondition.EQ_TEXT)) {
                throw new DbException("Relational operator expected (" + tokenPos + ")");
            }
            int relop = tokenType;

            nextToken();

            if ((tokenType < BOOLEAN) || (tokenType > STRING)) {
                throw new DbException("Value expected (" + tokenPos + ")");
            }

            Object value = null;
            switch (table.getField(field).getType()) {
                case DbField.BOOLEAN: {
                    if (tokenType != BOOLEAN) {
                        throw new DbException("Boolean value expected (" + tokenPos + ")");
                    }

                    value = new Boolean("TRUE".equals(tokenText));
                    break;
                }

                case DbField.INTEGER:
                case DbField.BITSET: {
                    if (tokenType != NUMBER) {
                        throw new DbException("Numeric value expected (" + tokenPos + ")");
                    }

                    value = new Integer(Integer.parseInt(tokenText));
                    break;
                }

                case DbField.LONG: {
                    if (tokenType != NUMBER) {
                        throw new DbException("Numeric value expected (" + tokenPos + ")");
                    }

                    value = new Long(Long.parseLong(tokenText));
                    break;
                }

                case DbField.DATETIME: {
                    long l = getDateTime(tokenText);

                    if ((l == -1) || (tokenType != NUMBER)) {
                        throw new DbException("Date/time value expected (" + tokenPos + ")");
                    }

                    break;
                }

                case DbField.STRING: {
                    value = tokenText;
                    break;
                }

                default: {
                    throw new DbException("Illegal field type (" + tokenPos + ")");
                }
            }

            nextToken();

            return new DbCondition(relop, field, value);
        }

        else if (tokenType == LPAR) {
            nextToken();

            DbCondition temp = parseExpression();

            if (tokenType != RPAR) {
                throw new DbException("Unclosed parenthesis (" + tokenPos + ")");
            }

            nextToken();

            return temp;
        }

        else if (tokenType == DbCondition.NOT) {
            nextToken();

            return new DbCondition(DbCondition.NOT, new DbCondition[] {parseExpression()});
        }

        else {
            throw new DbException("Identifier, parenthesis or NOT expected (" + tokenPos + ")");
        }
    }
}
