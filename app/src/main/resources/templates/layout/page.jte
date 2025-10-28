@import hexlet.code.util.NamedRoutes
@import hexlet.code.dto.BasePage
@import gg.jte.Content

@param Content content
@param BasePage page = null

<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Анализатор страниц</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-sRIl4kxILFvY47J16cr9ZwB07vP4J8+LH7qKQnuqkuIAvNWLzeN8tE5YBujZqJLB" crossorigin="anonymous">
</head>
<body class="d-flex flex-column min-vh-100 bg-light">
<header class="sticky-top">
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container-fluid">
            <a class="navbar-brand" href="${NamedRoutes.rootPath()}">Анализатор страниц</a>

            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="${NamedRoutes.rootPath()}">Главная</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="/urls">Сайты</a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>
</header>

@if(page != null && page.getFlash() != null)
<div class="container-fluid px-0">
    @if(page.getFlashType() == "success")
    <div class="alert alert-success rounded-0 mb-0 alert-dismissible" role="alert">
        ${page.getFlash()}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    @elseif(page.getFlashType() == "error")
    <div class="alert alert-danger rounded-0 mb-0 alert-dismissible" role="alert">
        ${page.getFlash()}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    @elseif(page.getFlashType() == "warning")
    <div class="alert alert-warning rounded-0 mb-0 alert-dismissible" role="alert">
        ${page.getFlash()}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    @else
    <div class="alert alert-info rounded-0 mb-0 alert-dismissible" role="alert">
        ${page.getFlash()}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    @endif
</div>
@endif

<main class="flex-grow-1">
    <div class="container-lg h-100 w-100 d-flex flex-column align-items-center justify-content-center">
        ${content}
    </div>
</main>

<footer class="text-center text-light bg-dark py-3 mt-auto">
    <div class="container-fluid">
        <div class="d-flex flex-column gap-2 flex-md-row justify-content-center align-items-center small">
            <span>© ${java.time.Year.now().getValue()} <strong>Екатерина Авдеенко</strong></span>
            <span class="d-none d-md-inline">|</span>
            <span class="text-break">Анализатор страниц -
                <span class="d-block d-md-inline">учебный проект на <a href="https://ru.hexlet.io/" target="_blank" class="link-light link-underline-opacity-0">Hexlet</a></span>
            </span>
            <span class="d-none d-md-inline">|</span>
            <span><a href="https://github.com/Katherini17/java-project-72" target="_blank" class="link-light">GitHub</a></span>
            <span class="d-none d-md-inline">|</span>
            <span class="d-block d-md-inline">Katherini17@yandex.ru</span>
        </div>
    </div>
</footer>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js" integrity="sha384-FKyoEForCGlyvwx9Hj09JcYn3nv7wiPVlz7YYwJrWVcXK/BmnVDxM+D2scQbITxI" crossorigin="anonymous"></script>
</body>
</html>