class UserModel {
  final String uid;
  final String email;
  final String? displayName;
  final String? photoUrl;
  final DateTime createdAt;
  final int securityScore;
  final int scansCount;
  final int threatsDetected;

  UserModel({
    required this.uid,
    required this.email,
    this.displayName,
    this.photoUrl,
    required this.createdAt,
    this.securityScore = 100,
    this.scansCount = 0,
    this.threatsDetected = 0,
  });

  factory UserModel.fromJson(Map<String, dynamic> json) {
    return UserModel(
      uid: json['uid'] as String,
      email: json['email'] as String,
      displayName: json['displayName'] as String?,
      photoUrl: json['photoUrl'] as String?,
      createdAt: json['createdAt'] != null 
          ? DateTime.parse(json['createdAt'] as String) 
          : DateTime.now(),
      securityScore: json['securityScore'] as int? ?? 100,
      scansCount: json['scansCount'] as int? ?? 0,
      threatsDetected: json['threatsDetected'] as int? ?? 0,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'uid': uid,
      'email': email,
      'displayName': displayName,
      'photoUrl': photoUrl,
      'createdAt': createdAt.toIso8601String(),
      'securityScore': securityScore,
      'scansCount': scansCount,
      'threatsDetected': threatsDetected,
    };
  }

  UserModel copyWith({
    String? uid,
    String? email,
    String? displayName,
    String? photoUrl,
    DateTime? createdAt,
    int? securityScore,
    int? scansCount,
    int? threatsDetected,
  }) {
    return UserModel(
      uid: uid ?? this.uid,
      email: email ?? this.email,
      displayName: displayName ?? this.displayName,
      photoUrl: photoUrl ?? this.photoUrl,
      createdAt: createdAt ?? this.createdAt,
      securityScore: securityScore ?? this.securityScore,
      scansCount: scansCount ?? this.scansCount,
      threatsDetected: threatsDetected ?? this.threatsDetected,
    );
  }
}
