// Mp3PlayerGenerator
// Java 17+ is required
// Home Page: https://github.com/pponec/Mp3PlayerGenerator/
// License: Apache License, Version 2.0

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Mp3PlayerGenerator {

    private final String homePage = "https://github.com/pponec/Mp3PlayerGenerator";
    private final String appName = getClass().getName();
    private final String appVersion = "1.3";
    private final String title = "MP3 player";
    private final String outputFile = "index.html";
    private final Locale locale = Locale.getDefault();
    private final Charset charset = StandardCharsets.UTF_8;

    public static void main(String[] args) throws Exception {
        var o = new Mp3PlayerGenerator();
        if (args.length > 0) {
            o.printHelpAndExit();
        }
        var mp3List = o.getSortedMp3Files();
        var html = o.buildHtmlPlayer(mp3List);
        Files.writeString(Paths.get(o.outputFile), html, o.charset);
    }

    private void printHelpAndExit() {
        System.out.println("Script '%s' v%s (%s)".formatted(appName, appVersion, homePage));
        System.out.println("Usage version: %s".formatted(appName));
        System.exit(1);
    }

    private CharSequence buildHtmlPlayer(List<String> mp3List) {
        var songFiles = mp3List.stream()
                .map(s -> "\"" + s + "\"")
                .collect(Collectors.joining("\n\t, "));
        var params = Map.of(
                "songFiles", songFiles,
                "title", title,
                "charset", charset,
                "appName", appName,
                "appVersion", appVersion,
                "homePage", homePage);
        return format(htmlTemplate(), params);
    }

    private List<String> getSortedMp3Files() {
        var files = new File(".").listFiles();
        if (files == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(files)
                .filter(file -> file.isFile())
                .filter(file -> file.getName().toLowerCase(locale).endsWith(".mp3"))
                .map(file -> file.getName())
                .sorted()
                .toList();
    }

    private String htmlTemplate() {
        return """
<!DOCTYPE html>
<html>
<head>
	<title>${title}</title>
	<meta charset="${charset}"/>
	<meta name="generator" content="${appName} v${appVersion}, ${homePage}"/>
	<style>
        h1 {
            color: steelblue;
        }
        #playlist li {
            cursor: pointer;
        }
        #playlist li.current {
            font-weight: bold;
        }
        #repeater {
            margin-left: 17px;
        }
        #audioPlayer {
            width: 100%;
            border: none;
        }
    </style>
</head>
<body>
	<h1>${title}</h1>
	<ol id="playlist"></ol>
	<label id="repeater">File repeater:
		<input type="checkbox"/>
	</label>
	<audio id="audioPlayer" controls></audio>
	<p id="currentSong"></p>
	<script>
        var playlist = [ ${songFiles} ];               
        var audioPlayer = document.getElementById('audioPlayer');
        var playlistElement = document.getElementById('playlist');
        var currentSongIndex = 0;
        var currentSongElement = document.getElementById('currentSong');
        var repeater = document.querySelector('#repeater input');

        // Generate playlist:
        playlist.forEach(function(song, index) {
            var listItem = document.createElement('li');
            listItem.innerText = 'file: ' + playlist[index];
            listItem.onclick = function() {
                playSong(index);
                // Disable a scrolling to the audio player:
                var currentPosition = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop || 0;
                audioPlayer.focus();
                window.scrollTo(0, currentPosition);
            };
            playlistElement.appendChild(listItem);
        });
        
        function playSong(index) {
            currentSongIndex = index;
            var songUrl = playlist[index];
            audioPlayer.src = encodeURI(songUrl);
            audioPlayer.play();
            updateCurrentSongText();
            updatePlaylistHighlight();
        }
        
        audioPlayer.addEventListener('ended', function() {
            if (!repeater.checked) {
                currentSongIndex = (currentSongIndex + 1) % playlist.length
            }
            playSong(currentSongIndex);
        });
        
        function updateCurrentSongText() {
            var title = 'Now playing: '  + (currentSongIndex + 1) + ". file: " + playlist[currentSongIndex];
            currentSongElement.innerText = title;
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
               """.stripIndent();
    }

    /** Join a template with arguments (a method from an Ujorm framework) */
    public final CharSequence format(String msg, Map<String, ?> args) {
        if (msg == null || args == null) {
            return String.valueOf(msg);
        }
        final var begTag = "${";
        final var endTag = '}';
        final var result = new StringBuilder(32 + msg.length());
        int i, last = 0;
        while ((i = msg.indexOf(begTag, last)) >= 0) {
            final var end = msg.indexOf(endTag, i);
            final var key = msg.substring(i + begTag.length(), end);
            final var val = args.get(key);
            if (val != null) {
                result.append(msg, last, i).append(val);
            } else {
                result.append(msg, last, end + 1);
            }
            last = end + 1;
        }
        return result.append(msg, last, msg.length());
    }
}