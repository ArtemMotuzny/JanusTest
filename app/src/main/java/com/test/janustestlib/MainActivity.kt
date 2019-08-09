package com.test.janustestlib

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.webrtc.*

class MainActivity : AppCompatActivity() {

    val janusClient: JanusClient = JanusManager(Retrofit(this).genService(JanusServer::class.java))
    val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        add_name.setOnClickListener {
            if (name.text.isNotBlank()) {
                scope.launch {
                    val rooms = janusClient.getRooms()
                    janusClient.connectionToRoom(rooms.plugindata.data.list[0].room, name.text.toString())
                    initWebrtc()
                }
            }
        }



        scope.launch {
            janusClient.connectToJanus()
        }

        keepAlive()
    }

    private fun initWebrtc() {

        PeerConnectionFactory.initialize(PeerConnectionFactory.InitializationOptions.builder(this).createInitializationOptions())
        val peerConnectionFactory = PeerConnectionFactory.builder().createPeerConnectionFactory()
        val constraints = MediaConstraints()
        val audioSource = peerConnectionFactory.createAudioSource(constraints)
        val audioTrack = peerConnectionFactory.createAudioTrack("101", audioSource)

        val rtcConfig = PeerConnection.RTCConfiguration(arrayListOf())

        val peerConnection = peerConnectionFactory.createPeerConnection(rtcConfig, object : PeerConnection.Observer {
            override fun onIceCandidate(p0: IceCandidate?) {
                scope.launch {
                    janusClient.sendIceCandidate(p0)
                }
                Log.d("PeerConnection", "onIceCandidate ${p0.toString()}")
            }

            override fun onDataChannel(p0: DataChannel?) {
                Log.d("PeerConnection", "onDataChannel ${p0.toString()}")
            }

            override fun onIceConnectionReceivingChange(p0: Boolean) {
                Log.d("PeerConnection", "onIceConnectionReceivingChange $p0")
            }

            override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {
                Log.d("PeerConnection", "onIceConnectionChange ${p0.toString()}")
            }

            override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {
                Log.d("PeerConnection", "onIceGatheringChange ${p0.toString()}")
            }

            override fun onAddStream(p0: MediaStream?) {
                Log.d("PeerConnection", "onAddStream ${p0.toString()}")
            }

            override fun onSignalingChange(p0: PeerConnection.SignalingState?) {
                Log.d("PeerConnection", "onSignalingChange ${p0.toString()}")
            }

            override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {
                Log.d("PeerConnection", "onIceCandidatesRemoved ${p0.toString()}")
            }

            override fun onRemoveStream(p0: MediaStream?) {
                Log.d("PeerConnection", "onRemoveStream ${p0.toString()}")
            }

            override fun onRenegotiationNeeded() {
                Log.d("PeerConnection", "onRenegotiationNeeded")
            }

            override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {
                Log.d("PeerConnection", "onAddTrack ${p0.toString()} and ${p1.toString()}")
            }
        })
        peerConnection?.addTrack(audioTrack)

        val sdpObserver = object : SdpObserver {
            override fun onSetFailure(p0: String?) {
                Log.d("SdpObserver", "onSetFailure ${p0.toString()}")
            }

            override fun onSetSuccess() {
                Log.d("SdpObserver", "onSetSuccess")
            }

            override fun onCreateSuccess(p0: SessionDescription?) {
                if (peerConnection?.localDescription == null) {
                    val t = this
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            peerConnection?.setLocalDescription(t, p0)
                            janusClient.createOffer(p0)
                        }
                    }
                }
                Log.d("SdpObserver", "onCreateSuccess ${p0.toString()}")
            }

            override fun onCreateFailure(p0: String?) {
                Log.d("SdpObserver", "onCreateFailure ${p0.toString()}")
            }
        }

        peerConnection?.createOffer(sdpObserver, constraints)
    }

    private fun keepAlive() {
        scope.launch(Dispatchers.IO){
            janusClient.keepAlive()
        }
    }
}
