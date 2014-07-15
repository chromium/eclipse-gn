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

import static org.junit.Assert.assertEquals;

import org.chromium.gnsupport.core.ast.GnAssignmentExpression;
import org.chromium.gnsupport.core.ast.GnMethodDeclaration;
import org.eclipse.dltk.ast.ASTListNode;
import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.expressions.BooleanLiteral;
import org.eclipse.dltk.ast.expressions.NumericLiteral;
import org.eclipse.dltk.ast.expressions.StringLiteral;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.junit.Test;

import java.util.List;

public class SyntaxParserTest extends AbstractParserTest {

  private static final int EOF_POSITION = -1;

  @Test
  public void testCommentNoNewline() throws Exception {
    ASTListNode root = parseClean("# Test");
    assertEquals(0, root.getChilds().size());
  }

  @Test
  public void testComment() throws Exception {
    ASTListNode root = parseClean("# Test\n");
    assertEquals(0, root.getChilds().size());
  }

  @Test
  public void testAssignmentNumber() throws Exception {
    ASTNode assignment = parseCleanSingleNode("foo = 1");
    assertNode(assignment, GnAssignmentExpression.class, 0, 7);
    assertNode(((GnAssignmentExpression) assignment).getRight(), NumericLiteral.class, 6, 7);
  }

  @Test
  public void testAssignmentString() throws Exception {
    ASTNode assignment = parseCleanSingleNode("foo = \"1\"");
    assertNode(assignment, GnAssignmentExpression.class, 0, 9);
    assertNode(((GnAssignmentExpression) assignment).getRight(), StringLiteral.class, 6, 9);
  }

  @Test
  public void testAssignmentBoolean() throws Exception {
    ASTNode assignment = parseCleanSingleNode("foo = true");
    assertNode(assignment, GnAssignmentExpression.class, 0, 10);
    assertNode(((GnAssignmentExpression) assignment).getRight(), BooleanLiteral.class, 6, 10);
  }

  @Test
  public void testNormalMethodDeclaration() throws Exception {
    String text = "func(\"target\") {" + "sources=[\"src.cc\"]" + "}";
    ASTNode node = parseCleanSingleNode(text);
    assertRange(node, 0, text.length());
    GnMethodDeclaration declaration = (GnMethodDeclaration) node;
    assertNode(declaration.getOriginalRef(), SimpleReference.class, 0, 4);
    assertNode(declaration.getRef(), SimpleReference.class, 0, 4);
  }

  @Test
  public void testTargetMethodDeclaration() throws Exception {
    String text = "action(\"target\") {}";
    ASTNode node = parseCleanSingleNode(text);
    assertRange(node, 0, text.length());
    GnMethodDeclaration declaration = (GnMethodDeclaration) node;
    assertNode(declaration.getOriginalRef(), SimpleReference.class, 0, 6);
    assertNode(declaration.getRef(), SimpleReference.class, 7, 15);
  }

  @Test
  public void testTemplateAndCallDeclaration() throws Exception {
    String text = "template(\"tplname\") {} tplname(\"target\") {}";
    ASTListNode nodes = parseClean(text);
    GnMethodDeclaration template = (GnMethodDeclaration) nodes.getChilds().get(0);
    GnMethodDeclaration templateCall = (GnMethodDeclaration) nodes.getChilds().get(1);
    assertRange(template.getOriginalRef(), 0, 8);
    assertRange(template.getRef(), 9, 18);
    assertRange(templateCall.getOriginalRef(), 23, 30);
    assertRange(templateCall.getRef(), 23, 30);
  }

  @Test
  public void testSingleIdentifier() throws Exception {
    TestProblemReporter reporter = new TestProblemReporter();
    ASTListNode nodes = parse("a", reporter);
    List<ASTNode> children = nodes.getChilds();
    assertEquals(0, children.size());
    assertSingleSyntaxError(EOF_POSITION, reporter);
  }

  @Test
  public void testIncompleteDeclarationAtEOF() throws Exception {
    TestProblemReporter reporter = new TestProblemReporter();
    ASTListNode nodes = parse("a = 1\nb =", reporter);
    List<ASTNode> children = nodes.getChilds();
    assertEquals(1, children.size());
    assertNode(children.get(0), GnAssignmentExpression.class, 0, 5);
    assertSingleSyntaxError(EOF_POSITION, reporter);
  }

  @Test
  public void testIncompleteSingleDeclarationAtEOF() throws Exception {
    TestProblemReporter reporter = new TestProblemReporter();
    ASTListNode nodes = parse("a =", reporter);
    List<ASTNode> children = nodes.getChilds();
    assertEquals(0, children.size());
    assertSingleSyntaxError(EOF_POSITION, reporter);
  }

  @Test
  public void testIncompleteList() throws Exception {
    TestProblemReporter reporter = new TestProblemReporter();
    String text = "a = [1, \nb = c";
    ASTListNode nodes = parse(text, reporter);
    List<ASTNode> children = nodes.getChilds();
    assertEquals(1, children.size());
    assertNode(children.get(0), GnAssignmentExpression.class, 0, text.length());
    assertSingleSyntaxError(EOF_POSITION, reporter);
  }

  private static void assertSingleSyntaxError(int offset, TestProblemReporter reporter) {
    assertEquals(1, reporter.getErrorCount());
    assertEquals("Syntax error", reporter.getLastMessage());
    assertEquals(offset, reporter.getLastErrorStart());
  }

  private static void assertNode(ASTNode node, Class<? extends ASTNode> clazz, int start, int end) {
    assertEquals(clazz, node.getClass());
    assertRange(node, start, end);
  }

  private static void assertRange(ASTNode node, int start, int end) {
    assertEquals(start, node.start());
    assertEquals(end, node.end());
  }

  private static ASTNode parseCleanSingleNode(String text) throws Exception {
    ASTListNode root = parseClean(text);
    List<ASTNode> children = root.getChilds();
    assertEquals(1, children.size());
    return children.get(0);
  }
}
