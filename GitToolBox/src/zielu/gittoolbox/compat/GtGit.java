package zielu.gittoolbox.compat;

import com.google.common.base.Preconditions;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Key;
import git4idea.commands.GitCommandResult;
import git4idea.commands.GitImpl;
import git4idea.commands.GitLineHandler;
import git4idea.commands.GitLineHandlerListener;
import git4idea.commands.GitLineHandlerPasswordRequestAware;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.jetbrains.annotations.NotNull;

public class GtGit {
    private final Project myProject;

    private GtGit(Project project) {
        myProject = project;
    }

    public static GtGit getInstance(@NotNull Project project) {
        return new GtGit(Preconditions.checkNotNull(project));
    }

    public GitCommandResult runRemoteCommand(Computable<GitLineHandler> command) {
        final List<String> errorOutput = new ArrayList<String>();
        final List<String> output = new ArrayList<String>();
        final AtomicInteger exitCode = new AtomicInteger();
        final AtomicBoolean startFailed = new AtomicBoolean();
        final AtomicReference exception = new AtomicReference();
        GitLineHandler handler = command.compute();
        handler.addLineListener(new GitLineHandlerListener() {
            public void onLineAvailable(String line, Key outputType) {
                if(isError(line)) {
                    errorOutput.add(line);
                } else {
                    output.add(line);
                }

            }
            public void processTerminated(int code) {
                exitCode.set(code);
            }
            public void startFailed(Throwable t) {
                startFailed.set(true);
                errorOutput.add("Failed to start Git process");
                exception.set(t);
            }
        });
        handler.runInCurrentThread((Runnable)null);
        if(handler instanceof GitLineHandlerPasswordRequestAware && ((GitLineHandlerPasswordRequestAware)handler).hadAuthRequest()) {
            errorOutput.add("Authentication failed");
        }

        boolean success = !startFailed.get() && errorOutput.isEmpty() && (handler.isIgnoredErrorCode(exitCode.get()) || exitCode.get() == 0);
        return new GitCommandResult(success, exitCode.get(), errorOutput, output, (Throwable)null);
    }

    private static boolean isError(String text) {
        for (String error : GitImpl.ERROR_INDICATORS) {
            if(text.startsWith(error.toLowerCase())) {
                return true;
            }
        }

        return false;
    }
}
