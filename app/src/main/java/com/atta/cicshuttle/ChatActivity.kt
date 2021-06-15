package com.atta.cicshuttle

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.atta.cicshuttle.adapters.MessagesAdapter
import com.atta.cicshuttle.databinding.ActivityChatBinding
import com.atta.cicshuttle.model.ChatChannel
import com.atta.cicshuttle.model.Message
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import java.util.*

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    private val TAG = "ChatActivity"

    private lateinit var db: FirebaseFirestore

    private lateinit var currentChannelId: String

    var messages: ArrayList<Message> = ArrayList()
    
    private lateinit var otherUserId: String

    lateinit var messagesAdapter: MessagesAdapter

    private lateinit var messagesListenerRegistration: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val title = "${SessionManager.with(this).getDriverName()} (${SessionManager.with(this).getRouteName()})"
        supportActionBar?.title = title

        otherUserId = SessionManager.with(this).getDriverId()
        db = FirebaseFirestore.getInstance()

        getOrCreateChatChannel()
    }

    private fun getOrCreateChatChannel(){
        db.collection("Users").document(SessionManager.with(this).getUserId())
                .collection("EngagedChatChannels")
                .document(otherUserId)
                .get()
                .addOnSuccessListener {
                    if (it.exists()){
                        currentChannelId = it["channelId"] as String

                    }else{

                        val newChannel =  db.collection("ChatChannels").document()

                        newChannel.set(ChatChannel(mutableListOf(SessionManager.with(this).getUserId(), otherUserId)))

                        db.collection("Users").document(SessionManager.with(this).getUserId())
                                .collection("EngagedChatChannels")
                                .document(otherUserId)
                                .set(mapOf("channelId" to newChannel.id))

                        db.collection("Drivers").document(otherUserId)
                                .collection("EngagedChatChannels")
                                .document(SessionManager.with(this).getUserId())
                                .set(mapOf("channelId" to newChannel.id))

                        currentChannelId = newChannel.id
                    }

                    getMessages()

                    binding.sendImg.setOnClickListener {
                        val msg = Message(binding.editTextMessage.text.toString(),
                                Timestamp(Calendar.getInstance().time),
                                SessionManager.with(this).getUserId(),
                                otherUserId, SessionManager.with(this).getUserName(),
                            "to driver")

                        binding.editTextMessage.setText("")

                        sendMessage(msg)
                    }

                }
    }

    private fun getMessages(){
        db.collection("ChatChannels").document(currentChannelId)
                .collection("messages")
                .get()
                .addOnSuccessListener {
                    if (!it.isEmpty){
                        for (document in it){

                            messages.add(document.toObject(Message::class.java))

                            //addRoute(route)
                        }
                        //checklists = it.toObjects<Checklist>()
                        showRecycler()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
    }

    private fun addChatMessagesListener() {

        val docRef = db.collection("ChatChannels").document(currentChannelId).collection("messages")
                .orderBy("time")

        docRef.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (firebaseFirestoreException != null) {
                        Log.e("FIRESTORE", "ChatMessagesListener error.", firebaseFirestoreException)
                        return@addSnapshotListener
                    }
            for (dc in querySnapshot!!.documentChanges) {
                when (dc.type) {
                    DocumentChange.Type.ADDED -> {
                        messages.add(dc.document.toObject(Message::class.java))
                        messagesAdapter.notifyDataSetChanged()
                    }

                }
            }


            binding.recyclerViewMessages.scrollToPosition(messagesAdapter.itemCount - 1)
        /*            val messages = mutableListOf<Message>()
                    querySnapshot.documents.forEach {
                        messages.add(it.toObject(Message::class.java)!!)
                    }
*/
                    //updateRecyclerView(messages)

              }
    }

    fun sendMessage(message: Message) {
        db.collection("ChatChannels").document(currentChannelId)
                .collection("messages")
                .add(message)
                .addOnSuccessListener {
                    db.collection("Users").document(SessionManager.with(this).getUserId())
                            .collection("EngagedChatChannels")
                            .document(otherUserId)
                            .set(mapOf("lastMessage" to message), SetOptions.merge())

                    db.collection("Drivers").document(otherUserId)
                            .collection("EngagedChatChannels")
                            .document(SessionManager.with(this).getUserId())
                            .set(mapOf("lastMessage" to message), SetOptions.merge())
                }


    }


    private fun showRecycler() {

        messagesAdapter = MessagesAdapter(messages, this@ChatActivity)
        binding.recyclerViewMessages.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = messagesAdapter
        }


        binding.recyclerViewMessages.scrollToPosition(messagesAdapter.itemCount - 1)

        addChatMessagesListener()
    }

    private fun updateRecyclerView(messages: List<Message>) {


            messagesAdapter.notifyDataSetChanged()


    }

}