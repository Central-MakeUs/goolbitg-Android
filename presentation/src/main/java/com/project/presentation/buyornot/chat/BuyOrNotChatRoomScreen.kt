package com.project.presentation.buyornot.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.project.domain.model.buyornot.BuyOrNotPostingModel
import com.project.domain.model.buyornot.ChatMessageModel
import com.project.presentation.R
import com.project.presentation.base.BaseIcon
import com.project.presentation.base.BaseTwoButtonPopup
import com.project.presentation.base.extension.ComposeExtension.noRippleClickable
import com.project.presentation.navigation.NavItem
import com.project.presentation.ui.theme.bg1
import com.project.presentation.ui.theme.black
import com.project.presentation.ui.theme.error
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray100
import com.project.presentation.ui.theme.gray200
import com.project.presentation.ui.theme.gray300
import com.project.presentation.ui.theme.gray400
import com.project.presentation.ui.theme.gray500
import com.project.presentation.ui.theme.gray600
import com.project.presentation.ui.theme.gray700
import com.project.presentation.ui.theme.main100
import com.project.presentation.ui.theme.white
import com.valentinilk.shimmer.shimmer
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

// ─── Stateful ────────────────────────────────────────────────────────────────
@Composable
fun BuyOrNotChatRoomScreen(
    postId: Int,
    navHostController: NavHostController = rememberNavController(),
    viewModel: BuyOrNotChatRoomViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showExitDialog by remember { mutableStateOf(false) }

    LaunchedEffect(postId) { viewModel.onEvent(BuyOrNotChatRoomEvent.InitPostId(postId)) }
    LaunchedEffect(Unit) {
        viewModel.navigateBack.collect { navHostController.popBackStack() }
    }

    Scaffold(
        containerColor = bg1,
        // 하단은 union(nav, ime) 로 직접 처리 → keyboard 와 입력바가 같이 자연스럽게 슬라이드
        contentWindowInsets = WindowInsets.statusBars
    ) { innerPadding ->
        BuyOrNotChatRoomContent(
            state = state,
            modifier = Modifier.padding(innerPadding),
            onBack = { navHostController.popBackStack() },
            onExitRequest = { showExitDialog = true },
            onEdit = {
                val posting = state.posting ?: return@BuyOrNotChatRoomContent
                navHostController.navigate(buildModifyRoute(posting))
            },
            onChangeInput = { viewModel.onEvent(BuyOrNotChatRoomEvent.ChangeInput(it)) },
            onSend = { viewModel.onEvent(BuyOrNotChatRoomEvent.SendMessage) }
        )
    }

    if (showExitDialog) {
        BaseTwoButtonPopup(
            title = "토론방 나가기",
            subTitle = "작심삼일 토론방을\n정말 나가시겠어요?",
            confirmText = "나가기",
            dismissText = "취소",
            confirmBgColor = error,
            confirmTextColor = white,
            dismissBgColor = gray400,
            dismissTextColor = gray100,
            onConfirm = {
                showExitDialog = false
                viewModel.onEvent(BuyOrNotChatRoomEvent.ExitChat)
            },
            onDismiss = { showExitDialog = false }
        )
    }
}

private fun buildModifyRoute(posting: BuyOrNotPostingModel): String {
    fun enc(v: String) = URLEncoder.encode(v, StandardCharsets.UTF_8.toString())
    return NavItem.BuyOrNotModifyPosting.route
        .replace("{postId}", "${posting.id}")
        .replace("{productName}", enc(posting.productName))
        .replace("{price}", "${posting.productPrice}")
        .replace("{imgUrl}", enc(posting.productImageUrl))
        .replace("{goodReason}", enc(posting.goodReason))
        .replace("{badReason}", enc(posting.badReason))
}

// ─── Stateless ───────────────────────────────────────────────────────────────
@Composable
fun BuyOrNotChatRoomContent(
    state: BuyOrNotChatRoomState,
    onBack: () -> Unit,
    onExitRequest: () -> Unit,
    onEdit: () -> Unit,
    onChangeInput: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            // 키보드와 네비게이션 바 중 큰 값을 적용 → 키보드가 올라오면 입력바도 같이 슬라이드
            .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))
    ) {
        // 1) 헤더 — 참여자에게만 우측 나가기 아이콘
        ChatRoomHeader(
            writerName = state.writerDisplayName,
            isWriter = state.isWriter,
            isReady = state.isReady,
            onBack = onBack,
            onExit = onExitRequest
        )

        // 2) 살까말까 mini 카드 — posting + 내 정보가 모두 도착해야 실제 카드 노출
        if (state.isReady && state.posting != null) {
            PostingMiniCard(
                posting = state.posting,
                isWriter = state.isWriter,
                onEdit = onEdit
            )
        } else {
            PostingMiniCardSkeleton()
        }

        // 3) 채팅 메시지 영역 — 준비되기 전엔 무조건 스켈레톤
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when {
                !state.isReady -> ChatMessagesSkeleton(
                    modifier = Modifier.fillMaxSize()
                )

                state.messages.isEmpty() -> EmptyChatPlaceholder(
                    modifier = Modifier.fillMaxSize()
                )

                else -> ChatMessageList(
                    modifier = Modifier.fillMaxSize(),
                    messages = state.messages,
                    currentUserId = state.currentUserId.orEmpty(),
                    currentUsername = state.currentUsername.orEmpty()
                )
            }
        }

        // 4) 입력 영역
        ChatInputBar(
            value = state.inputText,
            canSend = state.canSend,
            onValueChange = onChangeInput,
            onSend = onSend
        )
    }
}

// ─── 1) 헤더 ─────────────────────────────────────────────────────────────────
@Composable
private fun ChatRoomHeader(
    writerName: String,
    isWriter: Boolean,
    isReady: Boolean,
    onBack: () -> Unit,
    onExit: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BaseIcon(
            modifier = Modifier
                .size(32.dp)
                .noRippleClickable { onBack() },
            iconId = R.drawable.ic_arrow_left
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            text = if (!isReady || writerName.isBlank()) "" else "${writerName}님의 토론방",
            style = goolbitgTypography.h4,
            color = white,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        // 준비 전엔 우측 영역 노출 X (작성자 여부 결정 전 깜빡임 방지)
        if (!isReady) {
            Spacer(modifier = Modifier.size(32.dp))
        } else if (!isWriter) {
            BaseIcon(
                modifier = Modifier
                    .size(32.dp)
                    .noRippleClickable { onExit() },
                // exit 전용 아이콘이 없어 우측 화살표 placeholder. 추후 ic_logout 같은 에셋 추가 시 교체
                iconId = R.drawable.ic_chat_exit
            )
        } else {
            Spacer(modifier = Modifier.size(32.dp))
        }
    }
}

// ─── 2) 살까말까 mini 카드 ────────────────────────────────────────────────────
@Composable
private fun PostingMiniCard(
    posting: BuyOrNotPostingModel,
    isWriter: Boolean,
    onEdit: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(gray600))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 31.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = posting.productImageUrl,
                contentDescription = posting.productName,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(gray700)
            )
            Spacer(modifier = Modifier.width(9.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = posting.productName,
                    style = goolbitgTypography.body3,
                    color = white,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${formatPrice(posting.productPrice)}원",
                    style = goolbitgTypography.body5,
                    color = white
                )
            }
            // 작성자에게만 노출되는 white 강조 수정 버튼
            if (isWriter) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(white)
                        .noRippleClickable { onEdit() }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "수정",
                        style = goolbitgTypography.btn4,
                        color = black
                    )
                }
            }
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(gray600))
    }
}

@Composable
private fun PostingMiniCardSkeleton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .shimmer()
                .background(white.copy(alpha = 0.1f))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(width = 120.dp, height = 14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmer()
                    .background(white.copy(alpha = 0.1f))
            )
            Spacer(modifier = Modifier.size(6.dp))
            Box(
                modifier = Modifier
                    .size(width = 80.dp, height = 12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmer()
                    .background(white.copy(alpha = 0.1f))
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        // 수정 버튼 자리 (스켈레톤에서도 그대로 노출)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(white)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "수정", style = goolbitgTypography.btn4, color = black)
        }
    }
}

// ─── 3) 채팅 메시지 리스트 + 날짜 구분 ────────────────────────────────────────
@Composable
private fun ChatMessageList(
    messages: List<ChatMessageModel>,
    currentUserId: String,
    currentUsername: String,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    // reverseLayout=true 와 매칭되도록 데이터도 reverse — index 0 = 최신, 마지막 = 가장 오래된 메시지
    val reversed = remember(messages) { messages.asReversed() }
    // 새 메시지 도착 시 최신(index 0 = bottom) 으로 자동 스크롤
    LaunchedEffect(reversed.size) {
        if (reversed.isNotEmpty()) listState.animateScrollToItem(0)
    }
    LazyColumn(
        state = listState,
        // 키보드가 올라와 LazyColumn 의 높이가 줄어들 때, 최신 메시지가 입력바 바로 위에 anchored
        // 된 채로 같이 따라 올라온다. 상단 메시지부터 clip 되는 클래식 채팅 패턴.
        reverseLayout = true,
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        itemsIndexed(items = reversed, key = { _, msg -> msg.id }) { idx, message ->
            // reverse 된 리스트에서 chronological "이전" 은 +1 (한 칸 더 오래된 쪽)
            val chronoPrev = reversed.getOrNull(idx + 1)
            val showDateDivider = chronoPrev == null ||
                    chatDateKey(message.sentDateTime) != chatDateKey(chronoPrev.sentDateTime)
            // userId 가 있으면 userId 비교, 구버전 메시지엔 fallback 으로 username 비교
            val isMine = when {
                !message.userId.isNullOrBlank() && currentUserId.isNotBlank() ->
                    message.userId == currentUserId

                else -> message.username == currentUsername
            }
            Column {
                if (showDateDivider) {
                    DateDivider(
                        text = formatChatDate(message.sentDateTime),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                ChatMessageBubble(message = message, isMine = isMine)
            }
        }
    }
}

@Composable
private fun DateDivider(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(gray500)
        )
        Text(
            modifier = Modifier.padding(horizontal = 12.dp),
            text = text,
            style = goolbitgTypography.body5,
            color = gray300
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(gray500)
        )
    }
}

@Composable
private fun ChatMessageBubble(
    message: ChatMessageModel,
    isMine: Boolean
) {
    val timeText = remember(message.sentDateTime) { formatChatTime(message.sentDateTime) }
    if (isMine) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(text = timeText, style = goolbitgTypography.body5, color = gray400)
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 12.dp, topEnd = 4.dp,
                            bottomStart = 12.dp, bottomEnd = 12.dp
                        )
                    )
                    .background(main100)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = message.content,
                    style = goolbitgTypography.body4,
                    color = white
                )
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = message.username,
                    style = goolbitgTypography.body4,
                    color = white,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )
            }
            Row(verticalAlignment = Alignment.Bottom) {
                Box(
                    modifier = Modifier
                        .widthIn(max = 280.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 4.dp, topEnd = 12.dp,
                                bottomStart = 12.dp, bottomEnd = 12.dp
                            )
                        )
                        .background(gray600)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = message.content,
                        style = goolbitgTypography.body4,
                        color = white
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = timeText, style = goolbitgTypography.body5, color = gray400)
            }
        }
    }
}

@Composable
private fun ChatMessagesSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 가운데 날짜 구분 자리 (옅은 line + 박스)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(gray500))
            Box(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .size(width = 80.dp, height = 12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmer()
                    .background(white.copy(alpha = 0.1f))
            )
            Box(modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(gray500))
        }
        // 상대 메시지 placeholder
        SkeletonOtherMessage(width = 240.dp)
        SkeletonMyMessage(width = 220.dp)
        SkeletonOtherMessage(width = 200.dp)
        SkeletonMyMessage(width = 240.dp)
    }
}

@Composable
private fun SkeletonOtherMessage(width: androidx.compose.ui.unit.Dp) {
    Column {
        Box(
            modifier = Modifier
                .size(width = 60.dp, height = 12.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmer()
                .background(white.copy(alpha = 0.1f))
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Box(
                modifier = Modifier
                    .size(width = width, height = 36.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 4.dp, topEnd = 12.dp,
                            bottomStart = 12.dp, bottomEnd = 12.dp
                        )
                    )
                    .shimmer()
                    .background(white.copy(alpha = 0.1f))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(width = 40.dp, height = 12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmer()
                    .background(white.copy(alpha = 0.1f))
            )
        }
    }
}

@Composable
private fun SkeletonMyMessage(width: androidx.compose.ui.unit.Dp) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Bottom
    ) {
        Box(
            modifier = Modifier
                .size(width = 40.dp, height = 12.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmer()
                .background(white.copy(alpha = 0.1f))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Box(
            modifier = Modifier
                .size(width = width, height = 36.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 12.dp, topEnd = 4.dp,
                        bottomStart = 12.dp, bottomEnd = 12.dp
                    )
                )
                .shimmer()
                .background(white.copy(alpha = 0.1f))
        )
    }
}

@Composable
private fun EmptyChatPlaceholder(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "아직 채팅 내역이 없어요",
                style = goolbitgTypography.body3,
                color = gray300
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "첫 번째로 의견을 남겨보세요!",
                style = goolbitgTypography.body5,
                color = gray400
            )
        }
    }
}

// ─── 4) 하단 입력 영역 ───────────────────────────────────────────────────────
@Composable
private fun ChatInputBar(
    value: String,
    canSend: Boolean,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg1)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(gray700)
                .border(1.dp, gray500, RoundedCornerShape(20.dp))
                .heightIn(min = 40.dp)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            if (value.isEmpty()) {
                Text(
                    text = "메세지를 입력하세요",
                    style = goolbitgTypography.body4,
                    color = gray400
                )
            }
            BasicTextField(
                modifier = Modifier.fillMaxWidth(),
                value = value,
                onValueChange = onValueChange,
                textStyle = goolbitgTypography.body4.copy(color = white),
                cursorBrush = SolidColor(white),
                maxLines = 7
            )
        }
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (canSend) main100 else gray500)
                .let { if (canSend) it.noRippleClickable { onSend() } else it },
            contentAlignment = Alignment.Center
        ) {
            BaseIcon(
                modifier = Modifier.size(16.dp),
                iconId = R.drawable.ic_chat_send
            )
        }
    }
}

// ─── helpers ─────────────────────────────────────────────────────────────────
/** "2024-12-13T13:12:00" → "오후 13:12" / "오전 11:38" 형태 */
private fun formatChatTime(sentDateTime: String): String {
    return try {
        val timePart = sentDateTime.substringAfter("T").substringBefore(".")
        val parts = timePart.split(":")
        val hour = parts[0].toInt()
        val minute = parts[1]
        val ampm = if (hour < 12) "오전" else "오후"
        "$ampm $hour:$minute"
    } catch (e: Exception) {
        sentDateTime
    }
}

/** "2024-12-13T..." → "2024년 12월 13일" */
private fun formatChatDate(sentDateTime: String): String {
    return try {
        val datePart = sentDateTime.substringBefore("T")
        val parts = datePart.split("-")
        "${parts[0]}년 ${parts[1].toInt()}월 ${parts[2].toInt()}일"
    } catch (e: Exception) {
        ""
    }
}

/** 같은 날짜 메시지 그룹핑용 키 — "2024-12-13" 부분만 추출 */
private fun chatDateKey(sentDateTime: String): String =
    sentDateTime.substringBefore("T")

private fun formatPrice(value: Int): String {
    val s = value.toString()
    val sb = StringBuilder()
    val len = s.length
    for (i in 0 until len) {
        if (i > 0 && (len - i) % 3 == 0) sb.append(',')
        sb.append(s[i])
    }
    return sb.toString()
}

// ─── Preview ─────────────────────────────────────────────────────────────────
@Preview(widthDp = 400, heightDp = 800)
@Composable
private fun ChatRoomContentPreview() {
    BuyOrNotChatRoomContent(
        state = BuyOrNotChatRoomState(
            postId = 1,
            posting = BuyOrNotPostingModel(
                id = 1, writerId = "user1", productName = "테켓 후드티",
                productPrice = 89000, productImageUrl = "",
                goodReason = "오래 쓸 수 있어요", badReason = "비싸요",
                goodVoteCount = 0, badVoteCount = 0
            ),
            currentUserId = "user1",
            currentUsername = "바쁜굴비",
            messages = listOf(
                ChatMessageModel(
                    id = 1,
                    username = "거지굴비",
                    content = "지금 네이버쇼핑에서 파는게 더 저렴함\nhttps://fruitsfamily.com/product/1tp",
                    sentDateTime = "2024-12-13T11:38:00"
                ),
                ChatMessageModel(
                    id = 2,
                    username = "바쁜굴비",
                    content = "오 좋은 정보 감사합니다 :D",
                    sentDateTime = "2024-12-13T13:12:00"
                ),
                ChatMessageModel(
                    id = 3,
                    username = "깐깐굴비",
                    content = "근데 후드티 10장이나 있으니까, 후드집업이나 요즘 유행하는 사파리점퍼는 어떠세요? 님분위기랑 잘어울릴듯",
                    sentDateTime = "2024-12-13T13:14:00"
                ),
                ChatMessageModel(
                    id = 4,
                    username = "거지굴비",
                    content = "오 인정, 테켓은 요즘 흔해져서, 차라리 근본템 쟁겨두는거 추천드림",
                    sentDateTime = "2024-12-13T16:07:00"
                )
            )
        ),
        onBack = {}, onExitRequest = {}, onEdit = {}, onChangeInput = {}, onSend = {}
    )
}
