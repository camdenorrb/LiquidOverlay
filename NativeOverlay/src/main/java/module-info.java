open module PoderTech.overlay {

	requires kotlin.stdlib;
    requires jdk.incubator.foreign;
    requires java.desktop;
	requires kotlin.stdlib.jdk7;

	exports tech.poder.overlay.api;
	exports tech.poder.overlay.data;
	exports tech.poder.overlay.handles;
	exports tech.poder.overlay.overlay;
	exports tech.poder.overlay.overlay.base;
	exports tech.poder.overlay.structs;
	exports tech.poder.overlay.utils;
	exports tech.poder.overlay.values;
	exports tech.poder.overlay.window;
}