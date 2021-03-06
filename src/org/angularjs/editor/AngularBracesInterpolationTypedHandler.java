package org.angularjs.editor;

import com.intellij.codeInsight.CodeInsightSettings;
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.ide.highlighter.HtmlFileType;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.angularjs.settings.AngularJSConfig;

/**
 * Created by denofevil on 26/11/13.
 */
public class AngularBracesInterpolationTypedHandler extends TypedHandlerDelegate {
    @Override
    public Result beforeCharTyped(char c, Project project, Editor editor, PsiFile file, FileType fileType) {
        if(!CodeInsightSettings.getInstance().AUTOINSERT_PAIR_BRACKET) return TypedHandlerDelegate.Result.DEFAULT;
        if (file.getFileType() == HtmlFileType.INSTANCE) {
            if (c == '{') {
                boolean addWhiteSpaceBetweenBraces = AngularJSConfig.getInstance().INSERT_WHITESPACE;
                int offset = editor.getCaretModel().getOffset();
                String chars = editor.getDocument().getText();
                if (offset > 0 && (chars.charAt(offset - 1)) == '{') {
                    if (offset < 2 || (chars.charAt(offset - 2)) != '{') {
                        if (alreadyHasEnding(chars, offset)) {
                            return TypedHandlerDelegate.Result.CONTINUE;
                        } else {
                            String interpolation = addWhiteSpaceBetweenBraces ? "{  }" : "{}";

                            if (offset == chars.length() || (offset < chars.length() && chars.charAt(offset) != '}')) {
                                interpolation += "}";
                            }

                            EditorModificationUtil.typeInStringAtCaretHonorBlockSelection(editor, interpolation, true);
                            editor.getCaretModel().moveToOffset(offset + (addWhiteSpaceBetweenBraces ? 2 : 1));
                            return Result.STOP;

                        }
                    }

                }

            }

        }

        return TypedHandlerDelegate.Result.CONTINUE;
    }

    private boolean alreadyHasEnding(String chars, int offset) {
        int i = offset;
        while (i < chars.length() && (chars.charAt(i) != '{' && chars.charAt(i) != '}' && chars.charAt(i) != '\n')) {
            i++;
        }
        return i + 1 < chars.length() && chars.charAt(i) == '}' && chars.charAt(i + 1) == '}';
    }

}
