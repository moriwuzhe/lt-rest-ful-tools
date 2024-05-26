package org.lt.restful.navigation.action;

import com.intellij.ide.util.gotoByName.ChooseByNameItemProvider;
import com.intellij.ide.util.gotoByName.ChooseByNameModel;
import com.intellij.ide.util.gotoByName.ChooseByNamePopup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import org.lt.utils.ToolUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RestServiceChooseByNamePopup extends ChooseByNamePopup {
    public static final Key<RestServiceChooseByNamePopup> CHOOSE_BY_NAME_POPUP_IN_PROJECT_KEY = new Key<>("ChooseByNamePopup");

    protected RestServiceChooseByNamePopup(@Nullable Project project, @NotNull ChooseByNameModel model, @NotNull ChooseByNameItemProvider provider, @Nullable ChooseByNamePopup oldPopup, @Nullable String predefinedText, boolean mayRequestOpenInCurrentWindow, int initialIndex) {
        super(project, model, provider, oldPopup, predefinedText, mayRequestOpenInCurrentWindow, initialIndex);
    }

    public static RestServiceChooseByNamePopup createPopup(final Project project,
                                                           @NotNull final ChooseByNameModel model,
                                                           @NotNull ChooseByNameItemProvider provider,
                                                           @Nullable final String predefinedText,
                                                           boolean mayRequestOpenInCurrentWindow,
                                                           final int initialIndex) {
        if (ToolUtils.isNotEmpty(predefinedText)) {
            return new RestServiceChooseByNamePopup(project, model, provider, null, predefinedText, mayRequestOpenInCurrentWindow, initialIndex);
        }

        final RestServiceChooseByNamePopup oldPopup = project == null ? null : project.getUserData(CHOOSE_BY_NAME_POPUP_IN_PROJECT_KEY);
        if (oldPopup != null) {
            oldPopup.close(false);
        }
        RestServiceChooseByNamePopup newPopup = new RestServiceChooseByNamePopup(project, model, provider, oldPopup, predefinedText, mayRequestOpenInCurrentWindow, initialIndex);

        if (project != null) {
            project.putUserData(CHOOSE_BY_NAME_POPUP_IN_PROJECT_KEY, newPopup);
        }
        return newPopup;
    }

    @Override
    public @NotNull String transformPattern(@NotNull String pattern) {
        final ChooseByNameModel model = getModel();
        return getTransformedPattern(pattern, model);
    }

    public static @NotNull String getTransformedPattern(@NotNull String pattern, @NotNull ChooseByNameModel model) {
        if (!(model instanceof GotoRequestMappingModel)) {
            return pattern;
        }

        return ToolUtils.removeRedundancyMarkup(pattern);
    }


    @Nullable
    public String getMemberPattern() {
        final String enteredText = getTrimmedText();
        final int index = enteredText.lastIndexOf('#');
        if (index == -1) {
            return null;
        }

        String name = enteredText.substring(index + 1).trim();
        return ToolUtils.trimToNull(name);
    }


}