package com.test.janustestlib

import org.webrtc.DataChannel
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription

class JanusHandler {

    private var started = false
    private var trickle = true
    private var iceDone = false
    private var sdpSent = false

    private var myStream: MediaStream? = null
    private var remoteStream: MediaStream? = null
    private var mySdp: SessionDescription? = null
    private var peerConnection: PeerConnection? = null
    private var dataChannel: DataChannel? = null



}