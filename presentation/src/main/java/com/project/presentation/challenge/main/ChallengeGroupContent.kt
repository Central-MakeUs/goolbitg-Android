package com.project.presentation.challenge.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.project.domain.model.challenge.ChallengeGroupItemModel
import com.project.presentation.R
import com.project.presentation.base.BaseIcon
import com.project.presentation.base.BaseTintIcon
import com.project.presentation.base.extension.ComposeExtension.noRippleClickable
import com.project.presentation.base.extension.ComposeExtension.rippleClickable
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray300
import com.project.presentation.ui.theme.gray400
import com.project.presentation.ui.theme.gray50
import com.project.presentation.ui.theme.gray500
import com.project.presentation.ui.theme.main100
import com.project.presentation.ui.theme.roundSm
import com.project.presentation.ui.theme.white
import com.valentinilk.shimmer.shimmer

@Composable
fun ChallengeGroupContent(
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChallengeGroupViewModel = hiltViewModel()
) {
    val challengeGroups by viewModel.challengeGroups.collectAsStateWithLifecycle()

    StatelessChallengeGroupContent(
        challengeGroups = challengeGroups,
        modifier = modifier,
        onItemClick = {},
        onSearch = onSearch
    )
}

@Composable
private fun StatelessEmptyGroupContent(
    modifier: Modifier = Modifier,
    onSearch: () -> Unit
) {
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(49.dp))
        BaseIcon(modifier = Modifier.size(160.dp), iconId = R.drawable.ic_empty_challenge)
        Text(
            text = stringResource(R.string.challenge_group_no_three_day_hump_desciption),
            style = goolbitgTypography.body2,
            color = gray300
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            modifier = Modifier
                .clip(CircleShape)
                .background(brush = Brush.horizontalGradient(listOf(main100, Color(0xFF67BF4E))))
                .noRippleClickable { onSearch() }
                .padding(horizontal = 24.dp, vertical = 16.dp),
            text = stringResource(R.string.challenge_group_search_three_day_hump),
            style = goolbitgTypography.btn2,
            color = white
        )
    }
}

@Composable
private fun StatelessChallengeGroupContent(
    modifier: Modifier = Modifier,
    challengeGroups: List<ChallengeGroupItemModel>?,
    onItemClick: () -> Unit,
    onSearch: () -> Unit
) {
    if (challengeGroups?.isEmpty() == true) {
        StatelessEmptyGroupContent(
            modifier = modifier,
            onSearch = onSearch
        )
    } else {
        ChallengeGroupsListContent(
            modifier = modifier,
            challengeGroups = challengeGroups,
            onItemClick = onItemClick
        )
    }
}

@Composable
private fun ChallengeGroupsListContent(
    modifier: Modifier = Modifier,
    challengeGroups: List<ChallengeGroupItemModel>?,
    onItemClick: () -> Unit
) {
    var isOnlyJoined by remember { mutableStateOf(false) }
    val contentColor by remember(isOnlyJoined) {
        derivedStateOf {
            if (isOnlyJoined) main100 else gray400
        }
    }
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.challenge_group_header_title),
                style = goolbitgTypography.h3,
                color = gray50
            )

            Row(modifier = Modifier.noRippleClickable { isOnlyJoined = !isOnlyJoined }) {
                BaseTintIcon(
                    modifier = Modifier.size(16.dp),
                    iconId = R.drawable.ic_checkbox,
                    tint = contentColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.challenge_group_header_check_title),
                    style = goolbitgTypography.body3,
                    color = contentColor
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        if(challengeGroups == null) {
            ChallengeGroupsSkeleton()
        }
        challengeGroups?.let {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 24.dp)
            ) {
                items(items = it) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        ChallengeGroupItem(
                            modifier = Modifier.fillMaxWidth(),
                            title = it.title,
                            isLock = it.isHidden,
                            participantCount = it.peopleCount,
                            totalCount = it.maxSize,
                            tagString = it.getTagString(),
                            onItemClick = onItemClick
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(gray500)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChallengeGroupsSkeleton(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 24.dp)) {
        repeat(5) {
            ChallengeGroupItemSkeleton(modifier = Modifier.fillMaxWidth())
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(gray500)
            )
        }
    }
}

@Composable
private fun ChallengeGroupItemSkeleton(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(120.dp, 16.dp)
                    .clip(RoundedCornerShape(roundSm))
                    .shimmer()
                    .background(white.copy(alpha = 0.1f))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Box(
                    modifier = Modifier
                        .size(32.dp, 10.dp)
                        .clip(RoundedCornerShape(roundSm))
                        .shimmer()
                        .background(white.copy(alpha = 0.1f))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(72.dp, 10.dp)
                        .clip(RoundedCornerShape(roundSm))
                        .shimmer()
                        .background(white.copy(alpha = 0.05f))
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        BaseIcon(iconId = R.drawable.ic_arrow_right_over)
    }
}

@Composable
private fun ChallengeGroupItem(
    modifier: Modifier = Modifier,
    title: String,
    isLock: Boolean,
    participantCount: Int,
    totalCount: Int,
    tagString: String,
    onItemClick: () -> Unit = {}
) {
    Row(modifier = modifier
        .noRippleClickable { onItemClick() }
        .padding(horizontal = 8.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(text = title, style = goolbitgTypography.body2, color = gray50)
                if (isLock) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(gray500)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        BaseIcon(modifier = Modifier.size(12.dp), iconId = R.drawable.ic_lock)
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BaseIcon(iconId = R.drawable.ic_group_color)
                Text(
                    text = "${participantCount}/${totalCount}",
                    style = goolbitgTypography.btn4,
                    color = main100
                )
                Text(modifier = Modifier.width(14.dp), text = "・", color = gray400)
                Text(
                    modifier = Modifier.weight(1f),
                    text = tagString,
                    style = goolbitgTypography.btn4,
                    color = gray300
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        BaseIcon(iconId = R.drawable.ic_arrow_right_over)
    }
}
