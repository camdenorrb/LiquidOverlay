open module PoderTech.overlay {
    requires kotlin.stdlib;
    requires jdk.incubator.foreign;
    requires java.desktop;
    exports tech.poder.overlay.audio;
    exports tech.poder.overlay.video;
    exports tech.poder.overlay.general;
}