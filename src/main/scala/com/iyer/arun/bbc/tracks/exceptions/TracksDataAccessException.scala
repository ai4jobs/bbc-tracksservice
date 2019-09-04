package com.iyer.arun.bbc.tracks.exceptions

class TracksDataAccessException(shortMessage: String, exception: Exception) extends RuntimeException(shortMessage, exception) {
}
