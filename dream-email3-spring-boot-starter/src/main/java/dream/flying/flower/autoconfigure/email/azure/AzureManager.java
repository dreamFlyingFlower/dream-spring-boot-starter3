package dream.flying.flower.autoconfigure.email.azure;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import com.azure.identity.AzureAuthorityHosts;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.models.Attachment;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.MessageCollectionResponse;
import com.microsoft.graph.models.Recipient;
import com.microsoft.graph.models.User;
import com.microsoft.graph.models.UserCollectionResponse;
import com.microsoft.graph.serviceclient.GraphServiceClient;

import dream.flying.flower.autoconfigure.email.properties.AzureProperties;
import lombok.RequiredArgsConstructor;

/**
 * Azure禁止了非交互式的登录,若需要使用非交互式功能,需要给权限,同时使用powershell限制访问邮箱
 *
 * @author 飞花梦影
 * @date 2026-06-16 17:02:39
 */
@RequiredArgsConstructor
public class AzureManager {

	private String targetEmail = "xxxxx@xxxx.com";

	private final AzureProperties azureProperties;

	/**
	 * 使用Graph API读取邮件
	 * 
	 * @param args
	 */
	public void readMail(String[] args) {
		// 构建客户端凭证
		ClientSecretCredential credential = new ClientSecretCredentialBuilder().tenantId(azureProperties.getTenantId())
				.clientId(azureProperties.getClientId())
				.clientSecret(azureProperties.getClientSecret())
				.authorityHost(azureProperties.isChainRegion()
						? AzureAuthorityHosts.AZURE_CHINA : AzureAuthorityHosts.AZURE_PUBLIC_CLOUD)
				.build();

		GraphServiceClient graphClient = azureProperties.isChainRegion()
				? new GraphServiceClient(credential, new String[] { azureProperties.getChinaScope() })
				: new GraphServiceClient(credential, new String[] { azureProperties.getScope() });
		if (azureProperties.isChainRegion()) {
			graphClient.getRequestAdapter().setBaseUrl(azureProperties.getChinaBaseUrl());
		}

		User user = getUser(graphClient);
		if (null == user) {
			return;
		}

		// 筛选最近7天的未读邮件
		OffsetDateTime oneWeekAgo = OffsetDateTime.now().minusDays(7);

		// 获取目标用户的收件箱
		MessageCollectionResponse response = graphClient
				// 只获取应用的
				// .me()
				// 获取指定邮箱的
				.users()
				.byUserId(user.getId())
				.mailFolders()
				.byMailFolderId("inbox")
				.messages()
				.get(requestConfig -> {
					// 指定邮箱,效果等同于users().byUserId
					// requestConfig.queryParameters.filter = "mail eq '" + targetEmail + "'";
					// 只获取已读且7天前的邮件
					requestConfig.queryParameters.filter =
							String.format("isRead eq false and receivedDateTime ge %s", oneWeekAgo.toString());
					// 获取需要的数据
					requestConfig.queryParameters.select = new String[] { "id", "subject", "from", "toRecipients",
							"receivedDateTime", "isRead", "bodyPreview", "body" };

					// 分页
					requestConfig.queryParameters.top = 20;
					// 排序
					requestConfig.queryParameters.orderby = new String[] { "receivedDateTime DESC" };
				});

		Map<String, Message> latestByConversation = new HashMap<>();
		for (Message message : response.getValue()) {

			System.out.println("主题: " + message.getSubject());
			System.out.println("发件人: " + message.getFrom().getEmailAddress().getName());
			System.out.println("---");

			// 去重
			String convId = message.getConversationId();
			Message existing = latestByConversation.get(convId);
			if (existing == null || message.getReceivedDateTime().isAfter(existing.getReceivedDateTime())) {
				latestByConversation.put(convId, message);
			}

			// 获取收件人列表
			List<Recipient> toRecipients = message.getToRecipients();
			if (toRecipients != null && !toRecipients.isEmpty()) {
				StringBuilder toList = new StringBuilder();
				for (Recipient recipient : toRecipients) {
					if (recipient.getEmailAddress() != null) {
						if (toList.length() > 0)
							toList.append(", ");
						toList.append(recipient.getEmailAddress().getAddress());
					}
				}
				System.out.println("收件人: " + toList);
			}

			// 获取阅读状态
			Boolean isRead = message.getIsRead();
			System.out.println("状态: " + (isRead != null && isRead ? "已读" : "未读"));

			// 获取邮件 ID（用于后续操作）
			String messageId = message.getId();
			System.out.println("邮件ID: " + messageId);

			// 获取邮件的附件列表
			List<Attachment> attachments = message.getAttachments();
			attachments.forEach(t -> System.out.print(t.getContentType()));

			// 获取邮件正文预览
			String bodyPreview = message.getBodyPreview();
			if (bodyPreview != null && !bodyPreview.isEmpty()) {
				System.out.println("正文预览: " + bodyPreview.substring(0, Math.min(100, bodyPreview.length())) + "...");
			}

			// 获取邮件正文
			if (message != null && message.getBody() != null) {
				// body 有两种格式：text 或 html
				String contentType = message.getBody().getContentType().toString();
				String content = message.getBody().getContent();

				if ("HTML".equalsIgnoreCase(contentType)) {
					// 如果是 HTML，可以保留 HTML 标签或转为纯文本
					System.out.println(content);
				} else {
					// 纯文本格式
					System.out.println(content);
				}
			}

		}

		// 处理分页（如果还有更多邮件）
		String nextLink = response.getOdataNextLink();
		if (nextLink != null && !nextLink.isEmpty()) {
			System.out.println("\n还有更多邮件，下一页链接: " + nextLink);
			// 获取下一页...

			response = graphClient
					// 只获取应用的
					// .me()
					// 获取指定邮箱的
					.users()
					.byUserId(user.getId())
					.mailFolders()
					.byMailFolderId("inbox")
					.messages()
					.withUrl(nextLink)
					.get(requestConfig -> {
						// 指定邮箱,效果等同于users().byUserId
						// requestConfig.queryParameters.filter = "mail eq '" + targetEmail + "'";
						// 获取需要的数据
						requestConfig.queryParameters.select = new String[] { "id", "subject", "from", "toRecipients",
								"receivedDateTime", "isRead", "bodyPreview", "body" };

						// 分页
						requestConfig.queryParameters.top = 20;
						// 排序
						requestConfig.queryParameters.orderby = new String[] { "receivedDateTime DESC" };
					});

		}
	}

	public User getUser(GraphServiceClient graphClient) {
		UserCollectionResponse response = graphClient.users().get(requestConfig -> {
			requestConfig.queryParameters.filter = "mail eq '" + targetEmail + "'";
		});

		List<User> users = response.getValue();
		return CollectionUtils.isEmpty(users) ? null : users.get(0);
	}
}