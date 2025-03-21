import SwiftUI
import Shared

struct ContentView: View {
    @State private var logContainerSize = 20.0
    @State private var loading = false
    @State private var createDuration: Duration? = nil
    @State private var listEveryAccessDuration: Duration? = nil
    @State private var listOnceAccessDuration: Duration? = nil
    @State private var listIndirectAccessDuration: Duration? = nil
    @State private var mapEveryAccessDuration: Duration? = nil
    @State private var mapOnceAccessDuration: Duration? = nil
    @State private var mapIndirectAccessDuration: Duration? = nil

    var body: some View {
        VStack(spacing: 16) {
            Text("log₂(collection size)")
            HStack(spacing: 16) {
                Slider(value: $logContainerSize, in: 12...20, step: 1)
                Text("\(Int(logContainerSize))")
                    .frame(width: 32)
            }
            Button("Run Benchmark", action: {
                DispatchQueue.global(qos: .userInitiated).async {
                    loading = true
                    createDuration = nil
                    listEveryAccessDuration = nil
                    listOnceAccessDuration = nil
                    listIndirectAccessDuration = nil
                    mapEveryAccessDuration = nil
                    mapOnceAccessDuration = nil
                    mapIndirectAccessDuration = nil

                    let clock = ContinuousClock()
                    var data: Shared.Data? = nil
                    createDuration = clock.measure {
                        data = Shared.Data(logSize: Int32(logContainerSize))
                    }

                    listEveryAccessDuration = clock.measure {
                        var totalLength = 0
                        for i in 1...5 {
                            if data!.list.indices.contains(i) {
                                totalLength += data!.list[i].count
                            }
                        }
                    }

                    listOnceAccessDuration = clock.measure {
                        let list = data!.list
                        var totalLength = 0
                        for i in 1...5 {
                            if list.indices.contains(i) {
                                totalLength += list[i].count
                            }
                        }
                    }
                    
                    listIndirectAccessDuration = clock.measure {
                        var totalLength = 0
                        for i in 1...5 {
                            totalLength += data!.getFromList(index: Int32(i))?.count ?? 0
                        }
                    }

                    mapEveryAccessDuration = clock.measure {
                        var totalLength = 0
                        for key in ["lorem", "ipsum", "dolor", "sit", "amet"] {
                            totalLength += data!.map[key]?.count ?? 0
                        }
                    }

                    mapOnceAccessDuration = clock.measure {
                        let map = data!.map
                        var totalLength = 0
                        for key in ["lorem", "ipsum", "dolor", "sit", "amet"] {
                            totalLength += map[key]?.count ?? 0
                        }
                    }
                    
                    mapIndirectAccessDuration = clock.measure {
                        var totalLength = 0
                        for key in ["lorem", "ipsum", "dolor", "sit", "amet"] {
                            totalLength += data!.getFromMap(key: key)?.count ?? 0
                        }
                    }

                    loading = false
                }
            })

            HStack {
                Text("Create data")
                Spacer()
                DurationView(duration: createDuration, loading: loading)
            }
            HStack {
                Text("Access list, getting every time")
                Spacer()
                DurationView(duration: listEveryAccessDuration, loading: loading)
            }
            HStack {
                Text("Access list, getting once")
                Spacer()
                DurationView(duration: listOnceAccessDuration, loading: loading)
            }
            HStack {
                Text("Access list, getting indirectly")
                Spacer()
                DurationView(duration: listIndirectAccessDuration, loading: loading)
            }
            HStack {
                Text("Access map, getting every time")
                Spacer()
                DurationView(duration: mapEveryAccessDuration, loading: loading)
            }
            HStack {
                Text("Access map, getting once")
                Spacer()
                DurationView(duration: mapOnceAccessDuration, loading: loading)
            }
            HStack {
                Text("Access map, getting indirectly")
                Spacer()
                DurationView(duration: mapIndirectAccessDuration, loading: loading)
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
        .padding()
    }
}

private struct DurationView: View {
    let duration: Duration?
    let loading: Bool

    var body: some View {
        if let duration {
            Text(UtilKt.formatDuration(seconds: duration.components.seconds, attoseconds: duration.components.attoseconds))
        } else if loading {
            ProgressView()
        } else {
            Text("-")
        }
    }
}

#Preview {
    ContentView()
}
