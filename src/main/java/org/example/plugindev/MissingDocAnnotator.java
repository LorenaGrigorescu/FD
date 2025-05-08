package org.example.plugindev;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MissingDocAnnotator implements Annotator {

    private static final String ERROR_MESSAGE_JAVADOC = "The method is public and not annotated." +
            " The annotation should contain what the method should do, the role of each parameter that is passed to it (in case they exist) " +
            "and what does this method return (in case there is not a void function);";

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof PsiMethod method)) return;

        PsiIdentifier nameId = method.getNameIdentifier();
        if (nameId == null) return;

        if (method.hasModifierProperty(PsiModifier.PUBLIC) && method.getDocComment() == null) {
            holder.newAnnotation(HighlightSeverity.ERROR, ERROR_MESSAGE_JAVADOC)
                    .range(nameId)
                    .textAttributes(CodeInsightColors.ERRORS_ATTRIBUTES)
                    .create();
            return;
        }

        // Get all @param tags
        PsiDocComment docComment = method.getDocComment();
        if(docComment == null)
            return;
        PsiDocTag[] paramTags = docComment.findTagsByName("param");

        // Extract names of documented parameters
        List<String> documentedParams = new java.util.ArrayList<>();
        for (PsiDocTag tag : paramTags) {
            PsiElement[] dataElements = tag.getDataElements();
            if (dataElements.length > 0) {
                documentedParams.add(dataElements[0].getText());
            }
        }

        // Compare with actual method parameters
        for (PsiParameter parameter : method.getParameterList().getParameters()) {
            String paramName = parameter.getName();
            if (!documentedParams.contains(paramName)) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Parameter '" + paramName + "' is missing @param tag")
                        .range(nameId)
                        .textAttributes(CodeInsightColors.ERRORS_ATTRIBUTES)
                        .create();
            }
        }
    }

}
