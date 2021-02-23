'use strict';
 
//var Video = require('twilio-video');
const Video = Twilio.Video;

var activeRoom;
var previewTracks;
var identity;
var roomName;

// Attach the Tracks to the DOM.
function attachTracks(tracks, container) {
	 
  tracks.forEach(function(track) {
    if (track) {
      container.appendChild(track.attach());
    }
  });
}

// Attach the Participant's Tracks to the DOM.
function attachParticipantTracks(participant, container) {
//  var tracks = Array.from(participant.tracks.values()).map(function(
//    trackPublication
//  ) {
//    return trackPublication.track;
//  });
  var tracks = Array.from(participant.tracks.values());
  attachTracks(tracks, container);
}

// Detach the Tracks from the DOM.
function detachTracks(tracks) {
  tracks.forEach(function(track) {
    if (track) {
      track.detach().forEach(function(detachedElement) {
        detachedElement.remove();
      });
    }
  });
}

// Detach the Participant's Tracks from the DOM.
function detachParticipantTracks(participant) {
  var tracks = Array.from(participant.tracks.values()).map(function(
    trackPublication
  ) {
    return trackPublication.track;
  });
  detachTracks(tracks);
}

function gotDevices(mediaDevices) {
  const select = document.getElementById('video-devices');
  select.innerHTML = '';
  select.appendChild(document.createElement('option'));
  let count = 1;
  mediaDevices.forEach(mediaDevice => {
    if (mediaDevice.kind === 'videoinput') {
      const option = document.createElement('option');
      option.value = mediaDevice.deviceId;
      const label = mediaDevice.label || `Camera ${count++}`;
      const textNode = document.createTextNode(label);
      option.appendChild(textNode);
      select.appendChild(option);
    }
  });
}

// 
function stopTracks(tracks) {
  tracks.forEach(function(track) {
    if (track) { track.stop(); }
  })
}

// When we are about to transition away from this page, disconnect
// from the room, if joined.
window.addEventListener('beforeunload', leaveRoomIfJoined);
 
// Obtain a token from the server in order to connect to the Room.
//$.getJSON('/meetings/token', function(data) {
//  identity = data.identity;
//document.getElementById('room-controls').style.display = 'block';

  // Bind button to join Room.

 
  document.getElementById('button-join').onclick = function() {
 
	  roomName = document.getElementById('room-name').value;
	  var participantName = document.getElementById('participant-name').value;
	  $.getJSON('/meetings/'+roomName+'?participantName='+participantName, function(data) {
		  identity = data.identity;
		  document.getElementById('button-join').style.display = 'none';
		  document.getElementById('button-leave').style.display = 'inline-block';
		  
		  if (!roomName) {
	        alert('Please enter a room name.');
	        return;
	      }

	      log("Joining room '" + roomName + "'...");
	      var connectOptions = {
	        name: roomName,
	        logLevel: 'debug'
	      };

	      if (previewTracks) {
	        connectOptions.tracks = previewTracks;
	      }

	      // Join the Room with the token from the server and the
	      // LocalParticipant's Tracks.
	      Video.connect(data.token, connectOptions).then(roomJoined, function(error) {
	        log('Could not connect to Twilio: ' + error.message);
	      });
	  })
	  .done(function(result) {
	      console.log( "second success: "+result );
	  })
	  .fail(function(error) {
	  	console.log( "error: "+error );
	  })
	  .always(function() {
	      console.log( "complete" );
	  });
	  $('.mic').show();
  };

  // Bind button to leave Room.
  document.getElementById('button-leave').onclick = function() {
	  document.getElementById('button-join').style.display = 'inline-block';
	  document.getElementById('button-leave').style.display = 'none';
	  document.getElementById('mic').style.display = 'none';
    log('Leaving room...');
    activeRoom.disconnect();
    location.reload();
  };

function updateVideoDevice(event) {
  const select = event.target;
  const localParticipant = activeRoom.localParticipant;
  if (select.value !== '') {
    const tracks = Array.from(localParticipant.videoTracks.values()).map(
      function(trackPublication) {
        return trackPublication.track;
      }
    );
    localParticipant.unpublishTracks(tracks);
    log(localParticipant.identity + ' removed track: ' + tracks[0].kind);
    detachTracks(tracks);
    stopTracks(tracks);
    Video.createLocalVideoTrack({
      deviceId: { exact: select.value }
    }).then(function(localVideoTrack) {
      localParticipant.publishTrack(localVideoTrack);
      log(localParticipant.identity + ' added track: ' + localVideoTrack.kind);
      const previewContainer = document.getElementById('local-media');
      attachTracks([localVideoTrack], previewContainer);
    });
  }
}

// Successfully connected!
function roomJoined(room) {
	
  window.room = activeRoom = room;

  navigator.mediaDevices.enumerateDevices().then(gotDevices);
  const select = document.getElementById('video-devices');

  log("Joined as '" + identity + "'");
  document.getElementById('button-join').style.display = 'none';
  document.getElementById('button-leave').style.display = 'inline';

  // Attach LocalParticipant's Tracks, if not already attached.
  var previewContainer = document.getElementById('local-media');
  if (!previewContainer.querySelector('video')) {
    attachParticipantTracks(room.localParticipant, previewContainer);
  }

  // Attach the Tracks of the Room's Participants.
  room.participants.forEach(function(participant) {
    log("Already in Room: '" + participant.identity + "'");
    var previewContainer = document.getElementById('remote-media');
    attachParticipantTracks(participant, previewContainer);
  });

  // When a Participant joins the Room, log the event.
  room.on('participantConnected', function(participant) {
    log("Joining: '" + participant.identity + "'");
  });

  // When a Participant adds a Track, attach it to the DOM.
  room.on('trackSubscribed', function(track, trackPublication, participant) {
//    log(participant.identity + ' added track: ' + track.kind);
	  log(trackPublication.identity + ' added track: ' + track.kind);
	  
    var previewContainer = document.getElementById('remote-media');
    attachTracks([track], previewContainer);
  });

  // When a Participant removes a Track, detach it from the DOM.
  room.on('trackUnsubscribed', function(track, trackPublication, participant) {
//    log(participant.identity + ' removed track: ' + track.kind);
    log(trackPublication.identity + ' removed track: ' + track.kind);
    detachTracks([track]);
  });

  // When a Participant leaves the Room, detach its Tracks.
  room.on('participantDisconnected', function(participant) {
    log("Participant '" + participant.identity + "' left the room");
    detachParticipantTracks(participant);
  });

  // Once the LocalParticipant leaves the room, detach the Tracks
  // of all Participants, including that of the LocalParticipant.
  room.on('disconnected', function() {
    log('Left');

    if (previewTracks) {
      previewTracks.forEach(function(track) {
        track.stop();
      });
    }
    detachParticipantTracks(room.localParticipant);
    room.participants.forEach(detachParticipantTracks);
    activeRoom = null;
    document.getElementById('button-join').style.display = 'inline';
    document.getElementById('button-leave').style.display = 'none';
    select.removeEventListener('change', updateVideoDevice);
  });
 
}

// Preview LocalParticipant's Tracks.
document.getElementById('button-preview').onclick = function() {
	 
	 	debugger
	 
  var localTracksPromise = previewTracks
    ? Promise.resolve(previewTracks)
    : Video.createLocalTracks();

  localTracksPromise.then(
    function(tracks) {
      window.previewTracks = previewTracks = tracks;
      var previewContainer = document.getElementById('local-media');
      if (!previewContainer.querySelector('video')) {
        attachTracks(tracks, previewContainer);
      }
    },
    function(error) {
      console.error('Unable to access local media', error);
      log('Unable to access Camera and Microphone');
    }
  ); 
};

// Activity log.

function log(message) {
  var logDiv = document.getElementById('log');
  logDiv.innerHTML += '<p>&gt;&nbsp;' + message + '</p>';
  logDiv.scrollTop = logDiv.scrollHeight;
}


// Leave Room.
function leaveRoomIfJoined() {
  if (activeRoom) {
    activeRoom.disconnect();
   
  }

}
//Preview LocalParticipant's Tracks.
document.getElementById('button-preview').onclick = function() {
	 
	

	
  var localTracksPromise = previewTracks
    ? previewTracks.forEach(function(track) { track.enable(!track.isEnabled) })  //Toggle if exists
    : Video.createLocalTracks().then(function(tracks) {
    window.previewTracks = previewTracks = tracks;
    var previewContainer = document.getElementById('local-media');
    if (!previewContainer.querySelector('video')) {
      attachTracks(tracks, previewContainer);
    }
  }, function(error) {
    console.error('Unable to access local media', error);
    log('Unable to access Camera and Microphone');
  });
     
};
//Mute/Unmute audio

var audioControl = false;

 
function toggleAudio(element) {
  var ele = element;
 
  audioControl = !audioControl;
  if (audioControl) {
	  document.getElementById('unmute').style.display='none';
	  document.getElementById('mute').style.display='block';
	  room.localParticipant.audioTracks.forEach(function(
		      audioTrack,
		      key,
		      map
		    ) {
		      console.log("muting this users audio");
		      audioTrack.disable();
		    });
  } else {
	  document.getElementById('unmute').style.display='block';
	  document.getElementById('mute').style.display='none';
	  room.localParticipant.audioTracks.forEach(function(
		      audioTrack,
		      key,
		      map
		    ) {
		      console.log("Un muting this users audio");
		      audioTrack.enable();
		    });
  }
}
 
 
 
 