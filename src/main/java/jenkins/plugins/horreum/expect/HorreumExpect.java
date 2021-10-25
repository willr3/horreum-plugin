package jenkins.plugins.horreum.expect;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import com.cloudbees.plugins.credentials.common.AbstractIdCredentialsListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.URIRequirementBuilder;

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Item;
import hudson.security.ACL;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.ListBoxModel;
import hudson.util.ListBoxModel.Option;
import jenkins.plugins.horreum.HorreumBaseBuilder;
import jenkins.plugins.horreum.HorreumGlobalConfig;

public class HorreumExpect extends HorreumBaseBuilder<HorreumExpectConfig> {

	@DataBoundConstructor
	public HorreumExpect(@Nonnull String test,
								@Nonnull long timeout,
								@Nonnull String tags,
								@Nonnull String expectedBy,
								@Nonnull String backlink) {
		super(new HorreumExpectConfig(test, timeout, tags, expectedBy, backlink));
	}

	public String getTest() {
		return config.getTest();
	}

	@DataBoundSetter
	public void setTest(String test) {
		this.config.setTest(test);
	}

	public long getTimeout() {
		return config.getTimeout();
	}

	@DataBoundSetter
	public void setTimeout(long timeout) {
		this.config.setTimeout(timeout);
	}

	public String getTags() {
		return this.config.getTags();
	}

	@DataBoundSetter
	public void setTags(String tags) {
		this.config.setTags(tags);
	}

	public String getExpectedBy() {
		return config.getExpectedBy();
	}

	@DataBoundSetter
	public void setExpectedBy(String expectedBy) {
		this.config.setExpectedBy(expectedBy);
	}

	public String getBacklink() {
		return config.getBacklink();
	}

	@DataBoundSetter
	public void setBacklink(String backlink) {
		this.config.setBacklink(backlink);
	}

	@Override
	protected HorreumExpectExecutionContext createExecutionContext(AbstractBuild<?, ?> build, BuildListener listener, EnvVars envVars) {
		return HorreumExpectExecutionContext.from(config, envVars, listener);
	}

	@Extension
	public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
		public static final boolean abortOnFailure = true;
		public static final Boolean quiet = false;

		public DescriptorImpl() {
			load();
		}

		@Override
		public boolean isApplicable(Class<? extends AbstractProject> aClass) {
			return true;
		}

		@Override
		public String getDisplayName() {
			return "Horreum Expect Run";
		}

		public ListBoxModel doFillAuthenticationItems(@AncestorInPath Item project,
													  @QueryParameter String url) {
			return fillAuthenticationItems(project, url);
		}

		public static ListBoxModel fillAuthenticationItems(Item project, String url) {
			if (project == null || !project.hasPermission(Item.CONFIGURE)) {
				return new StandardListBoxModel();
			}

			List<Option> options = new ArrayList<>();

			options.add(new Option(HorreumGlobalConfig.get().getAuthentication().getKeyName()));

			AbstractIdCredentialsListBoxModel<StandardListBoxModel, StandardCredentials> items = new StandardListBoxModel()
					.includeEmptyValue()
					.includeAs(ACL.SYSTEM,
							project, StandardUsernamePasswordCredentials.class,
							URIRequirementBuilder.fromUri(url).build());
			items.addMissing(options);
			return items;
		}
	}
}
