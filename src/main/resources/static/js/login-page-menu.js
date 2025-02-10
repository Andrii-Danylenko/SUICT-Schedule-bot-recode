$(document).ready(function () {
    let facultyLabel = $('label[for="faculty"]').hide();
    let facultySelect = $('#faculty').prop('disabled', true).hide();

    let courseLabel = $('label[for="course"]').hide();
    let courseSelect = $('#course').prop('disabled', true).hide();

    let groupLabel = $('label[for="group"]').hide();
    let groupSelect = $('#group').prop('disabled', true).hide();
    let button = $('#submitButton').prop('disabled', true).hide();

    $('#institute').change(function () {
        let instituteId = $(this).val();

        facultyLabel.hide();
        facultySelect.empty().append('<option value="">Виберить факультет</option>').prop('disabled', true).hide();
        courseLabel.hide();
        courseSelect.empty().append('<option value="">Виберить курс</option>').prop('disabled', true).hide();
        groupLabel.hide();
        groupSelect.empty().append('<option value="">Виберить групу</option>').prop('disabled', true).hide();

        if (instituteId) {
            $.get('/get/faculties?instituteId=' + instituteId, function (faculties) {
                $.each(faculties, function (index, faculty) {
                    facultySelect.append('<option value="' + faculty.id + '">' + faculty.facultyName + '</option>');
                });
                facultyLabel.fadeIn(300);
                facultySelect.prop('disabled', false).fadeIn(300);
            });
        }
    });

    $('#faculty').change(function () {
        let facultyId = $(this).val();

        courseLabel.hide();
        courseSelect.empty().append('<option value="">Виберить курс</option>').prop('disabled', true).hide();
        groupLabel.hide();
        groupSelect.empty().append('<option value="">Виберить групу</option>').prop('disabled', true).hide();

        if (facultyId) {
            $.get('/get/courses?facultyId=' + facultyId, function (courses) {
                $.each(courses, function (index, course) {
                    courseSelect.append('<option value="' + course + '">' + course + '</option>');
                });
                courseLabel.fadeIn(300);
                courseSelect.prop('disabled', false).fadeIn(300);
            });
        }
    });

    $('#course').change(function () {
        let facultyId = $('#faculty').val();
        let course = $(this).val();

        groupLabel.hide();
        groupSelect.empty().append('<option value="">Виберить групу</option>').prop('disabled', true).hide();

        if (facultyId && course) {
            $.get('/get/groups?facultyId=' + facultyId + '&course=' + course, function (groups) {
                $.each(groups, function (index, group) {
                    groupSelect.append('<option value="' + group.id + '">' + group.name + '</option>');
                });
                groupLabel.fadeIn(300);
                groupSelect.prop('disabled', false).fadeIn(300);
            });
        }
    });
    $('#group').change(function () {
        button.prop('disabled', false).fadeIn(300);
    })
});