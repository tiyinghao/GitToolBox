package zielu.gittoolbox.ui.projectView;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.util.treeView.PresentableNodeDescriptor.ColoredFragment;
import com.intellij.ui.Gray;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.FontUtil;
import git4idea.repo.GitRepository;
import java.awt.Color;
import java.util.Optional;
import jodd.util.StringBand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zielu.gittoolbox.config.GitToolBoxConfig;
import zielu.gittoolbox.status.GitAheadBehindCount;

public class ColoredNodeDecoration extends NodeDecorationBase {

    public ColoredNodeDecoration(@NotNull GitToolBoxConfig config,
                                 @NotNull GitRepository repo,
                                 @Nullable GitAheadBehindCount aheadBehind) {
        super(config, repo, aheadBehind);
    }

    private ColoredFragment makeStatusFragment(boolean prefix) {
        int style = SimpleTextAttributes.STYLE_PLAIN;
        if (config.projectViewStatusBold) {
            style |= SimpleTextAttributes.STYLE_BOLD;
        }
        if (config.projectViewStatusItalic) {
            style |= SimpleTextAttributes.STYLE_ITALIC;
        }
        Color color = config.projectViewStatusCustomColor ? config.getProjectViewStatusColor() : Gray._128;
        SimpleTextAttributes attributes = new SimpleTextAttributes(style, color);
        StringBand status = getStatusText();
        if (prefix) {
            String statusTemp = status.toString();
            status.setIndex(0);
            status.append(FontUtil.spaceAndThinSpace()).append(statusTemp);
        }
        return new ColoredFragment(status.toString(), attributes);
    }

    private SimpleTextAttributes getLocationAttributes() {
        return SimpleTextAttributes.GRAY_ATTRIBUTES;
    }

    @Override
    public boolean apply(ProjectViewNode node, PresentationData data) {
        Optional<String> locationString = Optional.ofNullable(data.getLocationString());
        if (config.showProjectViewLocationPath) {
            if (config.showProjectViewStatusBeforeLocation) {
                data.addText(makeStatusFragment(true));
                locationString.ifPresent(l -> data.setLocationString("- " + l));
            } else {
                if (locationString.isPresent()) {
                    StringBand location = new StringBand(FontUtil.spaceAndThinSpace());
                    location.append(locationString.get());
                    location.append(" - ");
                    data.addText(location.toString(), getLocationAttributes());
                    data.addText(makeStatusFragment(false));
                    data.setLocationString("");
                } else {
                    data.addText(makeStatusFragment(true));
                }
            }
        } else {
            locationString.ifPresent(data::setTooltip);
            data.setLocationString("");
            data.addText(makeStatusFragment(true));
        }
        return true;
    }
}
