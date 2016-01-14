# Scan my Code!

![logo](http://spencer-shadley.github.io/img/portfolio/scanmycode.png "Logo for Scan my Code!")

## About

Scan handwritten text and produce either its output or any complilation errors in an Android application.

## Background

Created as part of a Capital One Hackathon on a team with three other members. The application was primarily intended to be used in an education setting where computers are scarce, such as a third world country. With Scan my Code! each student can independently write code on a piece of paper then, once they choose to run their code, they can use a classroom (or personal) smartphone to scan their code for output/errors. Other use cases could be a augmented reality version of this solution in which you can see the output of code on a whiteboard by wearing something such as a HoloLens or Google Glass.

## How it works

After taking a picture of handwritten code on an Android device, the image is uploaded to a Heroku server. On this server, Google's Tesseract uses OCR to convert the picture into raw text. This text is sent via a REST call to codepad.org (an online IDE) by simulating user input. Finally the result from codepad.org is retrieved by the server and sent back to the Android device.

## Video of the Hackathon

[Three minute highlight video of the hackathon (I'm at 0:04)](https://www.youtube.com/watch?v=c_h4Yl8FZaM)

![logo](https://raw.githubusercontent.com/spencer-shadley/Scan-my-Code/master/CameraTest/res/drawable-xxhdpi/ic_launcher.png "Team Icon")
