package zielu.gittoolbox.compat;

import com.intellij.dvcs.repo.Repository;
import com.intellij.dvcs.repo.VcsRepositoryManager;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsRoot;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.GitVcs;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RepositoryManager extends AbstractProjectComponent {
    public RepositoryManager(Project project) {
        super(project);
    }

    public static RepositoryManager getInstance(@NotNull Project project) {
        return project.getComponent(RepositoryManager.class);
    }

    @Nullable
    public GitRepository getRepositoryForFile(@NotNull Project project, @NotNull VirtualFile file) {
        Repository repo = getRepoForFile(project, file);
        if (repo != null && GitVcs.NAME.equals(repo.getVcs().getName())) {
            return (GitRepository) repo;
        }
        return null;
    }

    @Nullable
    private Repository getRepoForFile(@NotNull Project project, @NotNull VirtualFile file) {
        ProjectLevelVcsManager vcsManager = ProjectLevelVcsManager.getInstance(project);
        VcsRepositoryManager vcsRepositoryManager = VcsRepositoryManager.getInstance(project);
        final VcsRoot vcsRoot = vcsManager.getVcsRootObjectFor(file);
        return vcsRoot != null ? vcsRepositoryManager.getRepositoryForRootQuick(vcsRoot.getPath()) : null;
    }
}
