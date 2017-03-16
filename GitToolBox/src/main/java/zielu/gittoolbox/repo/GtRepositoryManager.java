package zielu.gittoolbox.repo;

import com.intellij.dvcs.repo.Repository;
import com.intellij.dvcs.repo.VcsRepositoryManager;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsRoot;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBusConnection;
import git4idea.GitVcs;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryChangeListener;
import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GtRepositoryManager extends AbstractProjectComponent implements GitRepositoryChangeListener {
    private final Map<GitRepository, GtConfig> myConfigs = new ConcurrentHashMap<GitRepository, GtConfig>();
    private MessageBusConnection myConnection;

    public GtRepositoryManager(Project project) {
        super(project);
    }

    @Override
    public void repositoryChanged(@NotNull GitRepository repository) {
        File configFile = new File(VfsUtilCore.virtualToIoFile(repository.getGitDir()), "config");
        GtConfig config = GtConfig.load(configFile);
        myConfigs.put(repository, config);
    }

    public java.util.Optional<GtConfig> configFor(GitRepository repository) {
        return Optional.ofNullable(myConfigs.get(repository));
    }

    @Override
    public void initComponent() {
        myConnection = myProject.getMessageBus().connect();
        myConnection.subscribe(GitRepository.GIT_REPO_CHANGE, this);
    }

    @Override
    public void disposeComponent() {
        if (myConnection != null) {
            myConnection.disconnect();
            myConnection = null;
        }
        myConfigs.clear();
    }

    public static GtRepositoryManager getInstance(@NotNull Project project) {
        return project.getComponent(GtRepositoryManager.class);
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
