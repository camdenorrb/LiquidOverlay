package dev.twelveoclock.liquidoverlay.gui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.twelveoclock.liquidoverlay.LIQUIPEDIA
import dev.twelveoclock.liquidoverlay.api.Liquipedia
import kotlinx.coroutines.runBlocking
import kotlin.math.roundToInt
import kotlin.random.Random

object GUI {

	val NAVIGATION_WIDTH = 250.dp

	val BACKGROUND_COLOR = Color(43, 54, 72)


	// https://developer.android.com/jetpack/compose
	internal fun createApplication() = application {
		Window(
			onCloseRequest = ::exitApplication,
			title = "LiquidOverlay",
			state = rememberWindowState(width = 1000.dp, height = 1000.dp, position = WindowPosition(Alignment.Center)),
			icon = painterResource("logo/logoOverlay.svg"),
		) {

			val section = remember { mutableStateOf(Section.HOME) }

			MaterialTheme {
				when (section.value) {
					Section.HOME -> HomeScreen(section)
					Section.OVERLAY -> OverlayScreen(section)
					Section.SETTINGS -> SettingsScreen(section)
				}
			}
		}
	}

	@Composable
	fun HomeScreen(section: MutableState<Section>) {

		val colPadding = 35.dp
		val firstColumnWidth = 800.dp

		val boxColor = Color(33, 41, 54)
		val iconColor = Color(86, 101, 127)
		val largeText = Color(175, 189, 209)

		NavigationMenu(section)

		Row(modifier = Modifier.padding(start = NAVIGATION_WIDTH).fillMaxSize().background(BACKGROUND_COLOR)) {

			// Holder column for left
			Column(modifier = Modifier.fillMaxHeight().padding(colPadding).width(firstColumnWidth)) {

				// Notification Box
				Column (modifier = Modifier.height(170.dp).fillMaxWidth().background(boxColor), horizontalAlignment = Alignment.CenterHorizontally){

					Spacer(Modifier.height(10.dp))
					Icon(
						painterResource("logo/notifications.svg"),
						tint = iconColor,
						contentDescription = "Notifications Icon"
					)
					Spacer(Modifier.height(10.dp))

					Text(
						text = "0",
						color = Color.White,
						fontSize = 35.sp,
						fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
					)

					Spacer(Modifier.height(10.dp))

					Text(
						text = "Notifications",
						color = iconColor,
						fontSize = 25.sp,
						fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
					)
				}

				Spacer(Modifier.height(50.dp))

				val playersNames = mutableListOf<String>()
				val playerNationality = mutableListOf<String>()
				val playerTeam = mutableListOf<Float>()

				// Getting Player info
				runBlocking {
					val playerInfo = LIQUIPEDIA.player(listOf(Liquipedia.Wiki.VALORANT))
					for (i in 0..5){
						var num = Random.nextInt(0, playerInfo.result.size)
						playersNames.add(playerInfo.result[num].name)
						playerNationality.add(playerInfo.result[num].nationality)
						playerTeam.add(playerInfo.result[num].earnings)
					}
				}

				// Top Player Box
				Column (modifier = Modifier.height(350.dp).fillMaxWidth().background(boxColor)) {
					Text(
						modifier = Modifier.offset(x = 25.dp, y = 25.dp),
						text = "Featured Players",
						color = largeText,
						fontSize = 27.sp,
						fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
					)

					Spacer(Modifier.height(30.dp))

					Text(
						modifier = Modifier.offset(x = 25.dp, y = 30.dp),
						text = playersNames[0] + ", " + playerNationality[0] + ", earnings: $" + playerTeam[0],
						color = Color.White,
						fontSize = 20.sp,
						fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
					)

					Spacer(Modifier.height(15.dp))

					Text(
						modifier = Modifier.offset(x = 25.dp, y = 30.dp),
						text = playersNames[1] + ", " + playerNationality[1] + ", earnings: $" + playerTeam[1],
						color = Color.White,
						fontSize = 20.sp,
						fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
					)
					Spacer(Modifier.height(15.dp))
					Text(
						modifier = Modifier.offset(x = 25.dp, y = 30.dp),
						text = playersNames[2] + ", " + playerNationality[2] + ", earnings: $" + playerTeam[2],
						color = Color.White,
						fontSize = 20.sp,
						fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
					)
					Spacer(Modifier.height(15.dp))
					Text(
						modifier = Modifier.offset(x = 25.dp, y = 30.dp),
						text = playersNames[3] + ", " + playerNationality[3] + ", earnings: $" + playerTeam[3],
						color = Color.White,
						fontSize = 20.sp,
						fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
					)
					Spacer(Modifier.height(15.dp))

					Text(
						modifier = Modifier.offset(x = 25.dp, y = 30.dp),
						text = playersNames[4] + ", " + playerNationality[4] + ", earnings: $" + playerTeam[4],
						color = Color.White,
						fontSize = 20.sp,
						fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
					)
					Spacer(Modifier.height(15.dp))

					Text(
						modifier = Modifier.offset(x = 25.dp, y = 30.dp),
						text = playersNames[5] + ", " + playerNationality[5] + ", earnings: $" + playerTeam[5],
						color = Color.White,
						fontSize = 20.sp,
						fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
					)
				}
				// For last item bottom left
				var sponsor = mutableListOf<String>()

				runBlocking {
					val tournamentInfo = LIQUIPEDIA.tournament(listOf(Liquipedia.Wiki.VALORANT))
					for (i in 0..3) {
						sponsor.add(tournamentInfo.result[Random.nextInt(0, tournamentInfo.result.size)].sponsors)
					}
				}

				Spacer(Modifier.height(50.dp))

				Column (modifier = Modifier.height(350.dp).fillMaxWidth().background(boxColor)) {
					Text(
						modifier = Modifier.offset(x = 25.dp, y = 25.dp),
						text = "Featured Sponsors",
						color = largeText,
						fontSize = 27.sp,
						fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
					)

					Spacer(Modifier.height(20.dp))
					Text(
						modifier = Modifier.offset(x = 25.dp, y = 25.dp),
						text = sponsor[0],
						color = Color.White,
						fontSize = 20.sp,
						fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
					)

					Spacer(Modifier.height(15.dp))
					Text(
						modifier = Modifier.offset(x = 25.dp, y = 25.dp),
						text = sponsor[1],
						color = Color.White,
						fontSize = 20.sp,
						fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
					)

					Spacer(Modifier.height(15.dp))
					Text(
						modifier = Modifier.offset(x = 25.dp, y = 25.dp),
						text = sponsor[2],
						color = Color.White,
						fontSize = 20.sp,
						fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
					)

					Spacer(Modifier.height(15.dp))
					Text(
						modifier = Modifier.offset(x = 25.dp, y = 25.dp),
						text = sponsor[3],
						color = Color.White,
						fontSize = 20.sp,
						fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
					)
				}
			}

			// Holder column for right
			Column(modifier = Modifier.padding(colPadding).fillMaxSize()) {
				Image(
					painterResource("image/calendar.svg"),
					"Calendar Image of Current Date",
					modifier = Modifier.fillMaxWidth(),
					contentScale = ContentScale.Crop
				)

				Spacer(Modifier.height(20.dp))

				Image(
					painterResource("image/upcoming.png"),
					"Upcoming Events",
					modifier = Modifier.fillMaxWidth(),
					contentScale = ContentScale.Crop
				)
			}
		}
	}

	@Composable
	fun OverlayScreen(section: MutableState<Section>) {

		val rowPadding = 5.dp
		val rowHeight = 150.dp
		val colPadding = 20.dp
		val colWidth = 200.dp

		val offColor = Color(33, 41, 54)
		val onColor = Color(81, 142, 240)
		val textColor = Color.White

		val notActivated = 0f
		val activated = 1f

		val footstepsOffsetX = 0.dp
		val footstepsOffsetY = 225.dp
		val gunshotsOffsetX = 0.dp
		val gunshotsOffsetY = 175.dp

		var captionsActivated by remember { mutableStateOf(false) }
		var footstepsActivated by remember { mutableStateOf(false) }
		var gunshotsActivated by remember { mutableStateOf(false) }
		var translateActivated by remember { mutableStateOf(false) }
		var musicActivated by remember { mutableStateOf(false) }

		NavigationMenu(section)

		Column(modifier = Modifier.padding(start = NAVIGATION_WIDTH).fillMaxSize().background(BACKGROUND_COLOR)){

			// Menu
			Row(
				modifier = Modifier.padding(rowPadding).fillMaxWidth().height(rowHeight),
				horizontalArrangement = Arrangement.Center
			) {

				// Captions
				Column(
					modifier = Modifier.padding(colPadding).fillMaxHeight().width(colWidth).background(if (captionsActivated) onColor else offColor).clickable { captionsActivated = !captionsActivated},
					horizontalAlignment = Alignment.CenterHorizontally){

					Icon(
						painterResource("font-icons/captions.svg"),
						modifier = Modifier.padding(5.dp),
						tint = textColor,
						contentDescription = "Captions",
					)

					Text(
						text = "Captions",
						color = textColor,
						fontSize = 20.sp,
						fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
					)
				}

				// Visual Footsteps
				Column(
					modifier = Modifier.padding(colPadding).fillMaxHeight().width(colWidth).background(if (footstepsActivated) onColor else offColor).clickable { footstepsActivated = !footstepsActivated},
					horizontalAlignment = Alignment.CenterHorizontally){

					Icon(
						painterResource("font-icons/shoe.svg"),
						modifier = Modifier.padding(10.dp),
						tint = textColor,
						contentDescription = "Visual Footsteps",
					)

					Text(
						text = "Visual Footsteps",
						color = textColor,
						fontSize = 20.sp,
						fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
					)
				}

				// Visual Gunshots
				Column(
					modifier = Modifier.padding(colPadding).fillMaxHeight().width(colWidth).background(if (gunshotsActivated) onColor else offColor).clickable { gunshotsActivated = !gunshotsActivated},
					horizontalAlignment = Alignment.CenterHorizontally){

					Icon(
						painterResource("font-icons/gun.svg"),
						modifier = Modifier.padding(15.dp),
						tint = textColor,
						contentDescription = "Visual Gunshots",
					)

					Text(
						text = "Visual Gunshots",
						color = textColor,
						fontSize = 20.sp,
						fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
					)
				}

				// Translate
				Column(
					modifier = Modifier.padding(colPadding).fillMaxHeight().width(colWidth).background(if (translateActivated) onColor else offColor).clickable { translateActivated = !translateActivated},
					horizontalAlignment = Alignment.CenterHorizontally){

					Icon(
						painterResource("font-icons/translate.svg"),
						modifier = Modifier.padding(5.dp),
						tint = textColor,
						contentDescription = "Translate",
					)

					Text(
						text = "Translate",
						color = textColor,
						fontSize = 20.sp,
						fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
					)
				}

				// Music
				Column(
					modifier = Modifier.padding(colPadding).fillMaxHeight().width(colWidth).background(if (musicActivated) onColor else offColor).clickable { musicActivated = !musicActivated},
					horizontalAlignment = Alignment.CenterHorizontally){

					Icon(
						painterResource("font-icons/music.svg"),
						modifier = Modifier.padding(17.dp),
						tint = textColor,
						contentDescription = "Music"
					)

					Text(
						text = "Music",
						color = textColor,
						fontSize = 20.sp,
						fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
					)
				}
			}

			// Gameplay Image
			Row(
				modifier = Modifier.padding(rowPadding).fillMaxWidth().fillMaxHeight(),
				horizontalArrangement = Arrangement.Center
			) {

				Box {
					//Gameplay Image

					Image(
						painterResource("image/valorant.jpg"),
						"Valorant Gameplay Image",
						modifier = Modifier.fillMaxHeight(),
						contentScale = ContentScale.Crop
					)

					var captionOffsetX by remember { mutableStateOf(0f) }
					var captionOffsetY by remember { mutableStateOf(0f) }
					var translateOffsetX by remember { mutableStateOf(0f) }
					var translateOffsetY by remember { mutableStateOf(0f) }
					var musicOffsetX by remember { mutableStateOf(0f) }
					var musicOffsetY by remember { mutableStateOf(0f) }

					// Draggable Captions
					Image(
						painterResource("draggable/captions.svg"),
						"Draggable Captions",
						alpha = if (captionsActivated) activated else notActivated,
						modifier = Modifier
							.offset { IntOffset(captionOffsetX.roundToInt(), captionOffsetY.roundToInt()) }
							.pointerInput(Unit) {
								detectDragGestures { change, dragAmount ->
									change.consumeAllChanges()
									captionOffsetX += dragAmount.x
									captionOffsetY += dragAmount.y
								}
							}
					)

					// Draggable Translate
					Image(
						painterResource("draggable/translate.svg"),
						"Draggable Translation",
						alpha = if (translateActivated) activated else notActivated,
						modifier = Modifier
							.offset { IntOffset(translateOffsetX.roundToInt(), translateOffsetY.roundToInt()) }
							.pointerInput(Unit) {
								detectDragGestures { change, dragAmount ->
									change.consumeAllChanges()
									translateOffsetX += dragAmount.x
									translateOffsetY += dragAmount.y
								}
							}
					)

					// Draggable Music
					Image(
						painterResource("draggable/music.svg"),
						"Draggable Music",
						alpha = if (musicActivated) activated else notActivated,
						modifier = Modifier
							.offset { IntOffset(musicOffsetX.roundToInt(), musicOffsetY.roundToInt()) }
							.pointerInput(Unit) {
								detectDragGestures { change, dragAmount ->
									change.consumeAllChanges()
									musicOffsetX += dragAmount.x
									musicOffsetY += dragAmount.y
								}
							}
					)

					// Footsteps Toggle
					Image(
						painterResource("image/footprints.svg"),
						"Visual Footprints",
						alpha = if (footstepsActivated) activated else notActivated,
						modifier = Modifier.padding(horizontal = footstepsOffsetX, vertical = footstepsOffsetY)
					)

					// Gunshots Toggle
					Image(
						painterResource("image/gunshots.svg"),
						"Visual Gunshots",
						alpha = if (gunshotsActivated) activated else notActivated,
						modifier = Modifier.padding(horizontal = gunshotsOffsetX, vertical = gunshotsOffsetY)
					)

				}
			}
		}
	}

	@Composable
	fun SettingsScreen(section: MutableState<Section>) {

		NavigationMenu(section)

		val prefixColor = Color(81, 142, 240)
		val textColor = Color.White
		val rowColor = Color(33, 41, 54)
		val rowHeight = 50.dp
		val rowPadding = 20.dp

		Column(modifier = Modifier.padding(start = NAVIGATION_WIDTH).fillMaxSize().background(BACKGROUND_COLOR)){

			// Color blind
			Row(
				modifier = Modifier.padding(rowPadding).fillMaxWidth().height(rowHeight).background(rowColor),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceBetween,
			) {
				Row {
					Spacer(Modifier.width(10.dp))
					Text(
						text = "Color Blind",
						color = prefixColor,
						fontSize = 20.sp,
						fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
					)
					Spacer(Modifier.width(10.dp))
					Text(
						text = "for vibrant colors and sharper contrast",
						modifier = Modifier.padding(top = 5.dp),
						color = textColor,
						fontSize = 14.sp,
						fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
					)
				}

				Switch(
					checked = true,
					onCheckedChange = {},
				)
			}

			// Dark/Light
			Row(
				modifier = Modifier.padding(rowPadding).fillMaxWidth().height(rowHeight).background(rowColor),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceBetween,
			) {
				Row {
					Spacer(Modifier.width(10.dp))
					Text(
						text = "Dark/Light",
						color = prefixColor,
						fontSize = 20.sp,
						fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
					)
					Spacer(Modifier.width(10.dp))
					Text(
						text = "to prove if you’re a true gamer",
						modifier = Modifier.padding(top = 5.dp),
						color = textColor,
						fontSize = 14.sp,
						fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
					)
				}

				Switch(
					checked = true,
					onCheckedChange = {},
				)
			}

			// Language
			Row(
				modifier = Modifier.padding(rowPadding).fillMaxWidth().height(rowHeight).background(rowColor),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceBetween,
			) {
				Row {
					Spacer(Modifier.width(10.dp))
					Text(
						text = "Language",
						color = prefixColor,
						fontSize = 20.sp,
						fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
					)
					Spacer(Modifier.width(10.dp))
					Text(
						text = "for message transcription and translating",
						modifier = Modifier.padding(top = 5.dp),
						color = textColor,
						fontSize = 14.sp,
						fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
					)
				}

				val languages = listOf("English", "中文", "Español")
				var language : String by remember { mutableStateOf(languages[0]) }
				var expanded by remember { mutableStateOf(false) }

				Row (modifier = Modifier.padding(end = 10.dp, top = 10.dp).clickable{ expanded = !expanded }, horizontalArrangement = Arrangement.Center) {

					Text(
						text = language,
						modifier = Modifier.clickable { expanded = !expanded },
						color = Color.White,
						fontSize = 14.sp,
						fontFamily = FontFamily(Font("font/Roboto-Medium.ttf")),
					)
					Icon(
						imageVector = Icons.Rounded.ArrowDropDown,
						contentDescription = "DropDown",
						tint = Color.White,
						modifier = Modifier.size(30.dp).offset(x = 0.dp, y = -7.dp).clickable { expanded = !expanded}
					)

					DropdownMenu(
						expanded = expanded,
						onDismissRequest = {
							expanded = false
						}) {
						languages.forEach { mylanguage ->
							DropdownMenuItem(onClick = {
								expanded = false
								language = mylanguage
							}) {
								Text(mylanguage)
							}
						}
					}
				}
			}
		}
	}

	@Composable
	fun NavigationMenu(section: MutableState<Section>) {

		val prefixIconColor = Color.White
		val textColor = Color(86, 101, 127)
		val selectedColor = Color(25, 118, 210)

		val rowHeight = 40.dp

		val moveRight = 10.dp
		val fontSize = 17.sp

		//BitmapPainter(useResource())
		Column(Modifier.width(NAVIGATION_WIDTH).fillMaxHeight().background(Color(33, 41, 54))) {

			// Header with Icon
			Row(
				modifier = Modifier.height(100.dp).fillMaxWidth().offset(y = 20.dp),
				horizontalArrangement = Arrangement.Center
			) {
				Image(
					painterResource("logo/logoTransparent.svg"),
					"Liquid Overlay Icon"
				)
			}

			Divider(color = Color.Transparent, thickness = 50.dp)

			// Home Row
			Row(
				modifier = Modifier.height(rowHeight).fillMaxWidth().clickable { section.value = Section.HOME },
				verticalAlignment = Alignment.CenterVertically,
			) {

				// https://developer.android.com/reference/kotlin/androidx/compose/material/icons/Icons
				Icon(
					modifier = Modifier.offset(x = moveRight),
					imageVector = Icons.Rounded.Home,
					contentDescription = "Home",
					tint = if (section.value == Section.HOME) selectedColor else prefixIconColor
				)

				Spacer(Modifier.width(10.dp))

				Text(
					modifier = Modifier.offset(x = moveRight),
					text = "Home",
					color = if (section.value == Section.HOME) selectedColor else textColor,
					fontSize = fontSize,
					fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
				)

			}

			// Overlay Row
			Row(
				modifier = Modifier.height(rowHeight).fillMaxWidth().clickable { section.value = Section.OVERLAY },
				verticalAlignment = Alignment.CenterVertically
			) {

				// https://developer.android.com/reference/kotlin/androidx/compose/material/icons/Icons
				Icon(
					modifier = Modifier.offset(x = moveRight),
					painter = painterResource("font-icons/layers.svg"),
					contentDescription = "Overlay",
					tint = if (section.value == Section.OVERLAY) selectedColor else prefixIconColor
				)

				Spacer(Modifier.width(10.dp))

				Text(
					modifier = Modifier.offset(x = moveRight),
					text = "Overlay",
					color = if (section.value == Section.OVERLAY) selectedColor else textColor,
					fontSize = fontSize,
					fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
				)

			}

			// Settings Row
			Row(
				modifier = Modifier.height(rowHeight).fillMaxWidth().clickable { section.value = Section.SETTINGS },
				verticalAlignment = Alignment.CenterVertically
			) {

				// https://developer.android.com/reference/kotlin/androidx/compose/material/icons/Icons
				Icon(
					modifier = Modifier.offset(x = moveRight),
					imageVector = Icons.Rounded.Settings,
					contentDescription = "Settings",
					tint = if (section.value == Section.SETTINGS) selectedColor else prefixIconColor
				)

				Spacer(Modifier.width(10.dp))

				Text(
					modifier = Modifier.offset(x = moveRight),
					text = "Settings",
					color = if (section.value == Section.SETTINGS) selectedColor else textColor,
					fontSize = fontSize,
					fontFamily = FontFamily(Font("font/Roboto-Medium.ttf"))
				)
			}

			Divider(modifier = Modifier.offset(y = 10.dp), color = textColor, thickness = 1.dp)

			Spacer(Modifier.height(20.dp))

			Row(
				modifier = Modifier.height(rowHeight).fillMaxWidth().clickable { /*createOverlay()*/ },
				verticalAlignment = Alignment.CenterVertically
			) {
				Icon(
					modifier = Modifier.offset(x = moveRight),
					imageVector = Icons.Rounded.PlayArrow,
					contentDescription = "Start Overlay",
					tint = prefixIconColor
				)

				Spacer(Modifier.width(10.dp))

				Text(
					modifier = Modifier.offset(x = moveRight),
					text = "Start Overlay",
					fontSize = fontSize,
					fontFamily = FontFamily(Font("font/Roboto-Medium.ttf")),
					color = textColor
				)
			}
		}
	}


	enum class Section {
		HOME,
		OVERLAY,
		SETTINGS,
	}

}