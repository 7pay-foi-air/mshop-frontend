package hr.foi.air.mshop.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import hr.foi.air.image_loader.interfaces.IImageLoader
import hr.foi.air.mshop.imageloader.ImageLoaderManager
import hr.foi.air.mshop.ui.theme.Dimens
import hr.foi.air.mshop.ui.theme.MShopCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoaderPickerScreen(
    imageLoaderManager: ImageLoaderManager,
    onDismiss: () -> Unit,
    onModuleSelected: (IImageLoader) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.screenPadding)
                .padding(top = Dimens.lg, bottom = Dimens.xxl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.lg)
        ) {
            Text(
                text = "Odabir slike",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    Dimens.lg,
                    Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                imageLoaderManager.imageLoaders.forEach { module ->
                    LoaderModuleItem(
                        module = module,
                        modifier = Modifier
                            .width(100.dp),
                        onClick = {
                            onModuleSelected(module)
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoaderModuleItem(
    module: IImageLoader,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier,
        colors = MShopCard.elevatedColors(),
        shape = MShopCard.shape,
        elevation = MShopCard.elevatedElevation()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = module.icon,
                contentDescription = module.name,
                modifier = Modifier.size(Dimens.pickerIconSize),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(Dimens.sm))
            Text(
                text = module.name,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

