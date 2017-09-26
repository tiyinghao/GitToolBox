package zielu.gittoolbox.ui.projectView;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import git4idea.repo.GitRepository;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jetbrains.annotations.NotNull;
import zielu.gittoolbox.ProjectAware;
import zielu.gittoolbox.cache.PerRepoInfoCache;
import zielu.gittoolbox.cache.PerRepoStatusCacheListener;
import zielu.gittoolbox.cache.RepoInfo;
import zielu.gittoolbox.config.ConfigNotifier;
import zielu.gittoolbox.config.GitToolBoxConfig;
import zielu.gittoolbox.ui.util.AppUtil;

public class ProjectViewManager implements Disposable, ProjectAware {
    private final AtomicBoolean opened = new AtomicBoolean();
    private final Project myProject;
    private final MessageBusConnection myConnection;

    private ProjectViewManager(Project project) {
        this.myProject = project;
        myConnection = myProject.getMessageBus().connect();
        myConnection.subscribe(ConfigNotifier.CONFIG_TOPIC, new ConfigNotifier.Adapter() {
            @Override
            public void configChanged(GitToolBoxConfig config) {
                refreshProjectView();
            }
        });
        myConnection.subscribe(PerRepoInfoCache.CACHE_CHANGE, new PerRepoStatusCacheListener() {
            @Override
            public void stateChanged(@NotNull final RepoInfo info,
                                     @NotNull final GitRepository repository) {
                refreshProjectView();
            }
        });
    }

    private void refreshProjectView() {
        if (opened.get()) {
            AppUtil.invokeLaterIfNeeded(() -> {
                if (opened.get()) {
                    ProjectView.getInstance(myProject).refresh();
                }
            });
        }
    }

    public static ProjectViewManager create(Project project) {
        return new ProjectViewManager(project);
    }

    @Override
    public void opened() {
        opened.compareAndSet(false, true);
    }

    @Override
    public void closed() {
        opened.compareAndSet(true, false);
    }

    @Override
    public void dispose() {
        myConnection.disconnect();
    }
}
