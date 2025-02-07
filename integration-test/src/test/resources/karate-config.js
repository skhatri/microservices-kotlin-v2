function fn() {
    karate.configure('connectTimeout', 5000);
    karate.configure('readTimeout', 5000);
    karate.configure('ssl', true);
    let port = karate.properties['server.port'] || '8080';
    var config = {
        baseUrl: `https://localhost:${port}`
    };

    return config;
}

