class ThreatModel {
  final String id;
  final String type;
  final String title;
  final String description;
  final String severity; // HIGH, MEDIUM, LOW
  final DateTime timestamp;
  final bool isResolved;

  ThreatModel({
    required this.id,
    required this.type,
    required this.title,
    required this.description,
    required this.severity,
    required this.timestamp,
    this.isResolved = false,
  });

  factory ThreatModel.fromJson(Map<String, dynamic> json) {
    return ThreatModel(
      id: json['id'] as String,
      type: json['type'] as String,
      title: json['title'] as String,
      description: json['description'] as String,
      severity: json['severity'] as String,
      timestamp: DateTime.parse(json['timestamp'] as String),
      isResolved: json['isResolved'] as bool? ?? false,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'type': type,
      'title': title,
      'description': description,
      'severity': severity,
      'timestamp': timestamp.toIso8601String(),
      'isResolved': isResolved,
    };
  }
  
  ThreatModel copyWith({
    String? id,
    String? type,
    String? title,
    String? description,
    String? severity,
    DateTime? timestamp,
    bool? isResolved,
  }) {
    return ThreatModel(
      id: id ?? this.id,
      type: type ?? this.type,
      title: title ?? this.title,
      description: description ?? this.description,
      severity: severity ?? this.severity,
      timestamp: timestamp ?? this.timestamp,
      isResolved: isResolved ?? this.isResolved,
    );
  }
}
