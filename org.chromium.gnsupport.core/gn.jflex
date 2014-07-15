/*
 * Copyright (c) 2014, The Chromium Authors
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.chromium.gnsupport.internal.core.parser;

import java.io.Reader;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.ComplexSymbolFactory.Location;
import java_cup.runtime.Symbol;


/**
 * The GN Lexer.
 */
@SuppressWarnings("nls")
%%

%class GnLexer
%unicode
%cupsym GnParserSymbols
%cup
%implements GnParserSymbols
%line
%column
%char

%eofval{
  return symbolFactory.newSymbol("EOF", GnParserSymbols.EOF);
%eofval}

%{
  public GnLexer(Reader reader, ComplexSymbolFactory symbolFactory) {
    this(reader);
    this.symbolFactory = symbolFactory;
  }

  private StringBuffer string = new StringBuffer();
  private int stringStartLine, stringStartColumn, stringStartOffset;
  private ComplexSymbolFactory symbolFactory;

  public Symbol symbol(String name, int code) {
    return symbolFactory.newSymbol(name, code, new Location(yyline + 1, yycolumn + 1, yychar), new Location(yyline + 1, yycolumn + yylength(), yychar + yylength()), code);
  }

  public Symbol symbol(String name, int code, Object lexem) {
    return symbolFactory.newSymbol(name, code, new Location(yyline + 1, yycolumn + 1, yychar), new Location(yyline + 1, yycolumn + yylength(), yychar + yylength()), lexem);
  }

  public Symbol stringSymbol(int code, String lexem) {
    return symbolFactory.newSymbol("STRING:" + lexem, code, new Location(stringStartLine + 1, stringStartColumn + 1, stringStartOffset), new Location(yyline + 1, yycolumn + 1, yychar + 1), lexem);
  }
%}

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = {LineTerminator} | [ \t\f]

/* comments */
Comment = {EndOfLineComment}
EndOfLineComment = "#" {InputCharacter}* {LineTerminator}?

Identifier = [:jletter:] [:jletterdigit:]*

DecIntegerLiteral = 0 | [1-9][0-9]*
%state STRING

%%
/* keywords */
<YYINITIAL> "if"                 { return symbol("IF", IF); }
<YYINITIAL> "else"               { return symbol("ELSE", ELSE); }
<YYINITIAL> "true"               { return symbol("TRUE", TRUE); }
<YYINITIAL> "false"              { return symbol("FALSE", FALSE); }
<YYINITIAL> {
  /* identifiers */
  {Identifier}                   { return symbol("IDENT:" + yytext(), IDENTIFIER, yytext()); }

  /* literals */
  {DecIntegerLiteral}            { return symbol("INTEGER:" + yytext(), INTEGER_LITERAL, Long.valueOf(yytext())); }
  \"                             { string.setLength(0); stringStartLine = yyline; stringStartColumn = yycolumn; stringStartOffset = yychar; yybegin(STRING); }

  /* operators */
  "="                            { return symbol("EQ", EQ); }
  "+="                           { return symbol("PLUSEQ", PLUSEQ); }
  "-="                           { return symbol("MINUSEQ", MINUSEQ); }
  "+"                            { return symbol("PLUS", PLUS); }
  "-"                            { return symbol("MINUS", MINUS); }

  ","                            { return symbol("COMMA", COMMA); }
  "."                            { return symbol("DOT", DOT); }
  "("                            { return symbol("LPAREN", LPAREN); }
  ")"                            { return symbol("RPAREN", RPAREN); }
  "["                            { return symbol("LBRACKET", LBRACKET); }
  "]"                            { return symbol("RBRACKET", RBRACKET); }
  "{"                            { return symbol("LBRACE", LBRACE); }
  "}"                            { return symbol("RBRACE", RBRACE); }

  /* conditionals */
  "!"                            { return symbol("NOT", NOT); }
  "=="                           { return symbol("EQEQ", EQEQ); }
  "!="                           { return symbol("NOTEQ", NOTEQ); }
  ">"                            { return symbol("GT", GT); }
  ">="                           { return symbol("GTEQ", GTEQ); }
  "<"                            { return symbol("LT", LT); }
  "<="                           { return symbol("LTEQ", LTEQ); }
  "||"                           { return symbol("OROR", OROR); }
  "&&"                           { return symbol("ANDAND", ANDAND); }

  /* comments */
  {Comment}                      { /* ignore */ }

  /* whitespace */
  {WhiteSpace}                   { /* ignore */ }
}

<STRING> {
  \"                             { yybegin(YYINITIAL);
                                   return stringSymbol(STRING_LITERAL, string.toString()); }
  [^\n\r\"\\]+                   { string.append( yytext() ); }
  \\t                            { string.append('\t'); }
  \\n                            { string.append('\n'); }

  \\r                            { string.append('\r'); }
  \\\"                           { string.append('\"'); }
  \\                             { string.append('\\'); }
}

 /* error fallback */
[^]                              { return symbol("INVALID", INVALID); }
