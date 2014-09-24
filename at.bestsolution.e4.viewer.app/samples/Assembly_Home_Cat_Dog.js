var Media 		= Java.type("javafx.scene.media.Media");
var MediaPlayer = Java.type("javafx.scene.media.MediaPlayer");
var Transition 	= Java.type("org.eclipse.fx.ui.animation.BaseTransition");
var BiConsumer 	= Java.type("java.util.function.BiConsumer");
var Duration 	= Java.type("javafx.util.Duration");

var PropertyTransition = Java.extend(Transition);
var basePath;
		
function getRotateNode(node) {
	return node.getParent().getParent();
}
		
function getTransform(node,x,y,z) {
	for( var i = 0; i < node.getTransforms().size(); i++ ) {
		var t = node.getTransforms().get(i);
		if( t.getClass().getSimpleName().equals("Rotate") ) {
			if( t.getAxis().getX() == x 
				&& t.getAxis().getY() == y
				&& t.getAxis().getZ() == z ) {
				return t;
			}
		}
	}
	return null;
}
		
function rotateY(node, targetAngle, soundFile, duration) {
	var r = getTransform(node, 0, 1, 0);
	if( r != null ) {
		var start = r.getAngle();
		var delta = start - targetAngle;
		var transition = new PropertyTransition(Duration.millis(duration)) {
			interpolate: function(frac) {
				r.setAngle(start - frac * delta);
			}
		};
		transition.play();
		if( soundFile != null ) {
			var m = new Media(soundFile);
			var mediaPlayer = new MediaPlayer(m);
			mediaPlayer.play();
		}
	} 
}
		
function doorOpen(event) {
	var node = getRotateNode(event.target);
	rotateY(node,80.0, basePath +"/squeaking-door.mp3",3000);
}

function doorClose(event) {
	var node = getRotateNode(event.target);
	rotateY(node,180.0,null,3000);
}

function init() {
	var InitComsumer = Java.extend(BiConsumer);
	return new InitComsumer() {
		accept : function(n,l) {
			basePath = l;
			var dynamic = n.lookup("#dynamic_1");
			dynamic.setOnMousePressed(doorOpen);
		}
	};
}

init();