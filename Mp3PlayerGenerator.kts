#!/usr/bin/env kscript
package net.ponec.kotlin.utils.script

import java.io.File
import java.util.*
import kotlin.system.exitProcess

object MainSingleton1 {

    private val homePage = "https://github.com/pponec/Mp3PlayerGenerator"
    private val appName = "mp3-player-generator.kts"
    private val appVersion = "1.0"
    private val header = "MP3 player"
    private val outputFile = "index.html"
    private val newLine = System.lineSeparator()

    @JvmStatic
    fun main(args: Array<String>) {
        if (!args.isEmpty()) printHelpAndExit()

        val mp3List = getSortedMp3Files()
        val html = getHtmlPlayer(mp3List)

        File(outputFile).bufferedWriter().use { writer -> writer.write(html) }
    }

    fun printHelpAndExit() {
        println("Script '$appName' v$appVersion ($homePage)")
        println("Usage version: $appName")
        exitProcess(1)
    }

    fun getSortedMp3Files(): List<String> {
        val files = File(".").listFiles() ?: emptyArray()
        return files.asSequence()
            .filter { file -> file.isFile }
            .filter { file -> file.name.lowercase(Locale.getDefault()).endsWith(".mp3") }
            .map { file -> file.name }
            .sorted()
            .toList()
    }

    fun getHtmlPlayer(mp3List : List<String>) : String {
        val songArray = mp3List.joinToString("$newLine\t, ", "[", "]") { "\"$it\"" }
        return """
            <!DOCTYPE html>
            <html>
            <head>
              <title>$header</title>
              <style>
                h1 {
                  color: steelblue;
                }                
                #playlist {
                  list-style: none;
                }
                #playlist li {
                  cursor: pointer;
                }
                #playlist li.current {
                  font-weight: bold;
                }
              </style>
            </head>
            <body>
              <h1>$header</h1>
              <ul id="playlist"></ul>
              <audio id="audioPlayer" controls></audio>
              <p id="currentSong"></p>
            
              <script>
                var playlist = // MP3 sources
                    $songArray; 
                var audioPlayer = document.getElementById('audioPlayer');
                var playlistElement = document.getElementById('playlist');
                var currentSongIndex = 0;
                var currentSongElement = document.getElementById('currentSong');
            
                // Generate playlist:
                playlist.forEach(function(song, index) {
                  var listItem = document.createElement('li');
                  listItem.innerText = 'Song ' + playlist[index];
                  listItem.onclick = function() {
                    playSong(index);
                  };
                  playlistElement.appendChild(listItem);
                });
            
                function playSong(index) {
                  currentSongIndex = index;
                  var songUrl = playlist[index];
                  audioPlayer.src = songUrl;
                  audioPlayer.play();
                  updateCurrentSongText();
                  updatePlaylistHighlight();
                }
            
                audioPlayer.addEventListener('ended', function() {
                  currentSongIndex++;
                  if (currentSongIndex >= playlist.length) {
                    currentSongIndex = 0;
                  }
                  playSong(currentSongIndex);
                });
            
                function updateCurrentSongText() {
                  var currentSongText = 'Song ' + playlist[currentSongIndex];
                  currentSongElement.innerText = 'Now playing: ' + currentSongText;
                }
            
                function updatePlaylistHighlight() {
                  var playlistItems = playlistElement.getElementsByTagName('li');
                  for (var i = 0; i < playlistItems.length; i++) {
                    if (i === currentSongIndex) {
                      playlistItems[i].classList.add('current');
                    } else {
                      playlistItems[i].classList.remove('current');
                    }
                  }
                }
              </script>
            </body>
            </html>
        """.trimIndent()
    }
}

// Uncomment it for kscript:
MainSingleton1.main(args)


