package com.iyer.arun.bbc.tracks.domain

case class Track(trackType: String, id: String, urn: String, titles: Titles, availability: Availability)
