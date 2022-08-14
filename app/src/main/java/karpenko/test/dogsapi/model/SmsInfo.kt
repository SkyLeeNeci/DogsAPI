package karpenko.test.dogsapi.model

data class SmsInfo(
  var to: String,
  var text: String,
  var imageUrl: String?
)