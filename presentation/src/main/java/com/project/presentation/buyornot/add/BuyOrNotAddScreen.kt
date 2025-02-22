package com.project.presentation.buyornot.add

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.project.presentation.R
import com.project.presentation.base.BaseHeader
import com.project.presentation.base.BaseIcon
import com.project.presentation.base.BaseKeyboardBottomBtn
import com.project.presentation.base.BaseLoadingBox
import com.project.presentation.base.BaseTextField
import com.project.presentation.base.DismissKeyboardWrapper
import com.project.presentation.base.extension.ComposeExtension.fadingEdge
import com.project.presentation.base.extension.ComposeExtension.noRippleClickable
import com.project.presentation.base.keyboardAsState
import com.project.presentation.base.transformation.Transformation
import com.project.presentation.buyornot.add.CropActivity.Companion.CROP_IMAGE_RESULT_URI
import com.project.presentation.buyornot.add.CropActivity.Companion.TARGET_PHOTO_URI_KEY
import com.project.presentation.navigation.NavItem
import com.project.presentation.ui.theme.bg1
import com.project.presentation.ui.theme.goolbitgTypography
import com.project.presentation.ui.theme.gray300
import com.project.presentation.ui.theme.gray50
import com.project.presentation.ui.theme.gray500
import com.project.presentation.ui.theme.gray700
import com.project.presentation.ui.theme.transparent
import com.project.presentation.ui.theme.white

@OptIn(ExperimentalPermissionsApi::class)
@Composable
@Preview
fun BuyOrNotAddScreen(
    navHostController: NavHostController = rememberNavController(),
    viewModel: BuyOrNotAddViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val permissionState =
        rememberPermissionState(
            permission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                READ_MEDIA_IMAGES
            } else {
                READ_EXTERNAL_STORAGE
            },
        )

    val cropLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val cropImgUri = result.data?.getStringExtra(CROP_IMAGE_RESULT_URI)
                if (cropImgUri != null) {
                    viewModel.onEvent(BuyOrNotAddEvent.ChangeCropUri(cropImgUri))
                }
            }
        }

    val launcher =
        rememberLauncherForActivityResult(
            contract =
            ActivityResultContracts.GetContent(),
        ) { uri: Uri? ->
            try {
                if (uri != null) {
                    val intent = Intent(context, CropActivity::class.java).apply {
                        putExtra(TARGET_PHOTO_URI_KEY, uri.toString())
                    }
                    cropLauncher.launch(intent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    val state = viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.value.isPostingSuccess) {
        if (state.value.isPostingSuccess) {
            if(state.value.postId != null){
                navHostController.previousBackStackEntry?.savedStateHandle?.apply{
                    set("postId", viewModel.state.value.postId)
                    set("imgUrl", viewModel.state.value.resultImg)
                    set("productName", viewModel.state.value.productName)
                    set("price", viewModel.state.value.price.toInt())
                    set("goodReason", viewModel.state.value.goodReason)
                    set("badReason", viewModel.state.value.badReason)
                }
                navHostController.popBackStack()
            }else{
                val route = NavItem.BuyOrNot.route.replace("{tabIdx}", (viewModel.state.value.tabIdx?:0).toString())
                navHostController.navigate(route){
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg1)
            .imePadding()
    ) {
        DismissKeyboardWrapper{
            Scaffold(containerColor = transparent) { innerPadding ->
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)) {
                    BaseHeader(
                        modifier = Modifier.fillMaxWidth(),
                        title = stringResource(R.string.buyornot_add_header_title),
                        onBackPressed = { navHostController.popBackStack() }
                    )
                    BuyOrNotAddContent(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        state = state,
                        onAddPhoto = {
                            if (permissionState.status.isGranted) {
                                val type = "image/*"
                                launcher.launch(type)
                            } else {
                                permissionState.launchPermissionRequest()
                            }
                        },
                        onDeletePhoto = {
                            viewModel.onEvent(BuyOrNotAddEvent.InitImgUrl)
                        },
                        onProductNameChanged = {
                            viewModel.onEvent(BuyOrNotAddEvent.ChangeProductName(it))
                        },
                        onPriceChanged = {
                            viewModel.onEvent(BuyOrNotAddEvent.ChangePrice(it))
                        },
                        onGoodReasonChanged = {
                            viewModel.onEvent(BuyOrNotAddEvent.ChangeGoodReason(it))
                        },
                        onBadReasonChanged = {
                            viewModel.onEvent(BuyOrNotAddEvent.ChangeBadReason(it))
                        }
                    )

                    val keyboardState by keyboardAsState()
                    val focusManager = LocalFocusManager.current

                    BaseKeyboardBottomBtn(
                        modifier = Modifier
                            .fillMaxWidth(),
                        isKeyboard = keyboardState,
                        text = stringResource(R.string.buyornot_add_posting),
                        enabled = state.value.checkState(),
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.onEvent(BuyOrNotAddEvent.RequestAddPosting)
                        }
                    )
                }
            }
        }
        if(state.value.isLoading){
            BaseLoadingBox()
        }
    }
}

@Composable
private fun BuyOrNotAddContent(
    modifier: Modifier = Modifier,
    state: State<BuyOrNotAddState>,
    onAddPhoto: () -> Unit,
    onDeletePhoto: () -> Unit,
    onProductNameChanged: (String) -> Unit,
    onPriceChanged: (String) -> Unit,
    onGoodReasonChanged: (String) -> Unit,
    onBadReasonChanged: (String) -> Unit
) {
    val listState = rememberLazyListState()
    // 첫 번째 아이템과 마지막 아이템 가시성을 추적하는 상태
    val showTopFade by remember {
        derivedStateOf { listState.firstVisibleItemIndex != 0 || listState.firstVisibleItemScrollOffset != 0 }
    }
    val showBottomFade by remember {
        derivedStateOf {
            (listState.layoutInfo.totalItemsCount > 0 &&
                    listState.layoutInfo.visibleItemsInfo.lastOrNull()?.let { lastItem ->
                        lastItem.index == listState.layoutInfo.totalItemsCount - 1 &&
                                lastItem.offset + lastItem.size <= listState.layoutInfo.viewportEndOffset
                    } ?: true).not()
        }
    }

    val listFade by remember {
        derivedStateOf {
            when {
                showTopFade && showBottomFade -> Brush.verticalGradient(
                    0f to transparent,
                    0.03f to white
                )
                showTopFade -> Brush.verticalGradient(0f to transparent, 0.03f to white)
                else -> null
            }
        }
    }

    Column(modifier = modifier) {
        val url = if (state.value.currCroppedImgUri != null)
            state.value.currCroppedImgUri else state.value.prevImgUrl
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .then(
                    if (listFade != null) {
                        Modifier.fadingEdge(listFade!!)
                    } else {
                        Modifier
                    }
                ),
            state = listState
        ) {
            item {
                AddContentPhoto(
                    imgUrl = url,
                    onAddPhoto = onAddPhoto,
                    onDeletePhoto = onDeletePhoto
                )
                Spacer(modifier = Modifier.height(24.dp))
                AddContentProductName(
                    productName = state.value.productName,
                    onProductNameChanged = onProductNameChanged
                )
                Spacer(modifier = Modifier.height(24.dp))
                AddContentPrice(
                    price = state.value.price,
                    onPriceChanged = onPriceChanged
                )
                Spacer(modifier = Modifier.height(24.dp))
                AddContentGoodReason(
                    goodReason = state.value.goodReason,
                    onGoodReasonChanged = onGoodReasonChanged
                )
                Spacer(modifier = Modifier.height(24.dp))
                AddContentBadReason(
                    badReason = state.value.badReason,
                    onBadReasonChanged = onBadReasonChanged
                )
                Spacer(modifier = Modifier.height(65.dp))
            }
        }
    }
}

@Composable
private fun AddContentPhoto(
    modifier: Modifier = Modifier,
    imgUrl: String?,
    onAddPhoto: () -> Unit,
    onDeletePhoto: () -> Unit
) {
    Column(modifier = modifier.padding(horizontal = 24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.buyornot_add_photo_title),
                style = goolbitgTypography.caption1,
                color = gray50
            )
            Text(text = " *", style = goolbitgTypography.caption1, color = com.project.presentation.ui.theme.error)
        }

        if (imgUrl.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            AddContentPhotoEmpty(onClick = onAddPhoto)
        } else {
            AddContentPhotoFilled(imgUrl = imgUrl, onDeletePhoto = onDeletePhoto)
        }
    }
}

@Composable
private fun AddContentPhotoEmpty(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .size(120.dp)
            .drawBehind {
                drawRoundRect(
                    cornerRadius = CornerRadius(24f, 24f),
                    color = gray500, style = Stroke(
                        width = 4f, pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(12f, 12f), 0f
                        )
                    )
                )
            }
            .clip(RoundedCornerShape(9.dp))
            .background(gray700)
            .noRippleClickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        BaseIcon(iconId = R.drawable.ic_add_buyornot_photo)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.buyornot_add_photo_placeholder),
            color = gray300,
            style = goolbitgTypography.body3
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.buyornot_add_photo_placeholder_2),
            color = gray300,
            style = goolbitgTypography.body5
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun AddContentPhotoFilled(
    modifier: Modifier = Modifier,
    imgUrl: String,
    onDeletePhoto: () -> Unit
) {
    Box(modifier = modifier.size(130.dp)) {
        GlideImage(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(9.dp))
                .border(
                    width = 1.dp, color = gray500, shape = RoundedCornerShape(9.dp)
                )
                .align(Alignment.BottomStart),
            model = imgUrl,
            contentScale = ContentScale.Crop,
            contentDescription = null
        ) {
            it.skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
        }
        BaseIcon(modifier = Modifier
            .align(Alignment.TopEnd)
            .noRippleClickable {
                onDeletePhoto()
            }, iconId = R.drawable.ic_circle_close)
    }
}

@Composable
private fun AddContentProductName(
    modifier: Modifier = Modifier,
    productName: String,
    onProductNameChanged: (String) -> Unit
) {
    Column(modifier = modifier.padding(horizontal = 24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.buyornot_add_product_name_title),
                style = goolbitgTypography.caption1,
                color = gray50
            )
            Text(text = " *", style = goolbitgTypography.caption1, color = com.project.presentation.ui.theme.error)
        }
        Spacer(modifier = Modifier.height(8.dp))
        BaseTextField(
            modifier = Modifier.fillMaxWidth(),
            value = productName,
            placeHolderValue = stringResource(R.string.buyornot_add_product_name_placeholder),
            onTextChanged = onProductNameChanged,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
        )
    }
}

@Composable
private fun AddContentPrice(
    modifier: Modifier = Modifier,
    price: String,
    onPriceChanged: (String) -> Unit
) {
    Column(modifier = modifier.padding(horizontal = 24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.buyornot_add_price_name_title),
                style = goolbitgTypography.caption1,
                color = gray50
            )
            Text(text = " *", style = goolbitgTypography.caption1, color = com.project.presentation.ui.theme.error)
        }
        Spacer(modifier = Modifier.height(8.dp))
        BaseTextField(
            modifier = Modifier.fillMaxWidth(),
            value = price,
            prefixValue = "₩ ",
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number
            ),
            maxLength = 7,
            placeHolderValue = stringResource(R.string.buyornot_add_price_name_placeholder),
            onTextChanged = onPriceChanged,
            visualTransformation = Transformation.thousandSeparatorTransformation(),
        )
    }
}

@Composable
private fun AddContentGoodReason(
    modifier: Modifier = Modifier,
    goodReason: String,
    onGoodReasonChanged: (String) -> Unit
) {
    Column(modifier = modifier.padding(horizontal = 24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.buyornot_add_good_reason),
                style = goolbitgTypography.caption1,
                color = gray50
            )
            Text(text = " *", style = goolbitgTypography.caption1, color = com.project.presentation.ui.theme.error)
        }
        Spacer(modifier = Modifier.height(8.dp))
        BaseTextField(
            modifier = Modifier.fillMaxWidth(),
            value = goodReason,
            maxLength = 20,
            placeHolderValue = stringResource(R.string.buyornot_add_good_reason_placeholder),
            onTextChanged = onGoodReasonChanged,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
        )
    }
}

@Composable
private fun AddContentBadReason(
    modifier: Modifier = Modifier,
    badReason: String,
    onBadReasonChanged: (String) -> Unit
) {
    Column(modifier = modifier.padding(horizontal = 24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.buyornot_add_bad_reason),
                style = goolbitgTypography.caption1,
                color = gray50
            )
            Text(text = " *", style = goolbitgTypography.caption1, color = com.project.presentation.ui.theme.error)
        }
        Spacer(modifier = Modifier.height(8.dp))
        BaseTextField(
            modifier = Modifier.fillMaxWidth(),
            value = badReason,
            maxLength = 20,
            placeHolderValue = stringResource(R.string.buyornot_add_bad_reason_placeholder),
            onTextChanged = onBadReasonChanged,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
            ),
        )
    }
}
