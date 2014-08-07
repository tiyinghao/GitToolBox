package zielu.gittoolbox.compat;

import com.google.common.base.Preconditions;
import com.intellij.openapi.project.Project;
import git4idea.util.GitUIUtil;
import org.jetbrains.annotations.NotNull;

public class Notifier {
    private final Project myProject;

    private Notifier(Project project) {
        myProject = project;
    }

    public static Notifier getInstance(@NotNull Project project) {
        return new Notifier(Preconditions.checkNotNull(project));
    }

    public void notifySuccess(String message) {
        GitUIUtil.notifySuccess(myProject, "", message);
    }

    public void notifyError(String title, String message) {
        GitUIUtil.notifyImportantError(myProject, title, message);
    }

    public void notifyWeakError(String message) {
        GitUIUtil.notifyError(myProject, null, message);
    }
}
