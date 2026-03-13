'use strict';

$(document).ready(function() {
    // Константы
    const API_FORKLIFTS = '/api/forklifts';
    const API_DOWNTIMES = '/api/downtimes';
    
    // Состояние приложения
    const state = {
        forklifts: [],
        downtimes: [],
        selectedForkliftId: null,
        selectedDowntimeId: null,
        editMode: false,
        editDowntimeMode: false,
        originalForkliftData: null,
        originalDowntimeData: null
    };

    // Инициализация
    init();

    function init() {
        loadForklifts();
        setupEventListeners();
    }

    // Настройка обработчиков событий
    function setupEventListeners() {
        // Поиск
        $('#btnSearch').on('click', handleSearch);
        $('#btnReset').on('click', handleReset);
        $('#searchNumber').on('keypress', function(e) {
            if (e.which === 13) {
                handleSearch();
            }
        });

        // CRUD операции для погрузчиков
        $('#btnAdd').on('click', () => openForkliftModal('add'));

        // CRUD операции для простоев
        $('#btnAddDowntime').on('click', () => openDowntimeModal('add'));

        // Модальные окна
        $('#forkliftModalClose').on('click', () => closeModal('#forkliftModal'));
        $('#btnCancelForklift').on('click', () => closeModal('#forkliftModal'));
        $('#btnSaveForklift').on('click', handleSaveForkliftFromModal);

        $('#downtimeModalClose').on('click', () => closeModal('#downtimeModal'));
        $('#btnCancelDowntime').on('click', () => closeModal('#downtimeModal'));
        $('#btnSaveDowntime').on('click', handleSaveDowntimeFromModal);

        // Закрытие модальных окон по клику вне
        $('.modal').on('click', function(e) {
            if ($(e.target).hasClass('modal')) {
                closeModal('#' + $(this).attr('id'));
            }
        });

        // Выбор строки в таблице погрузчиков
        $('#forkliftTableBody').on('click', 'tr', function() {
            selectForkliftRow($(this).data('id'));
        });

        // Действия в таблице погрузчиков
        $('#forkliftTableBody').on('click', 'tr .action-btn', function(e) {
            e.stopPropagation();
            const tr = $(this).closest('tr');
            const id = tr.data('id');
            if ($(this).attr('title') === 'Редактировать') {
                state.selectedForkliftId = id;
                selectForkliftRow(id);
                openForkliftModal('edit');
            } else if ($(this).attr('title') === 'Удалить') {
                state.selectedForkliftId = id;
                handleDeleteForklift();
            }
        });

        // Выбор строки в таблице простоев
        $('#downtimeTableBody').on('click', 'tr', function() {
            selectDowntimeRow($(this).data('id'));
        });

        // Действия в таблице простоев
        $('#downtimeTableBody').on('click', 'tr .action-btn', function(e) {
            e.stopPropagation();
            const tr = $(this).closest('tr');
            const id = tr.data('id');
            if ($(this).attr('title') === 'Редактировать') {
                state.selectedDowntimeId = id;
                selectDowntimeRow(id);
                openDowntimeModal('edit');
            } else if ($(this).attr('title') === 'Удалить') {
                state.selectedDowntimeId = id;
                handleDeleteDowntime();
            }
        });

        // Кнопка профиля
        $('#btnProfile').on('click', function() {
            $('<div id="profileDialog" title="Профиль">' +
                '<p>Пользователь: ' + ($('#currentUser').val() || 'Администратор') + '</p>' +
                '<p>Роль: Администратор</p>' +
                '</div>').dialog({
                modal: true,
                width: 300,
                buttons: {
                    'ОК': function() {
                        $(this).dialog('close');
                    }
                },
                close: function() {
                    $(this).remove();
                }
            });
        });
    }

    // Загрузка списка погрузчиков
    function loadForklifts(searchNumber = null) {
        let url = API_FORKLIFTS;
        if (searchNumber) {
            url += '/search?number=' + encodeURIComponent(searchNumber);
        }

        $.ajax({
            url: url,
            method: 'GET',
            success: function(data) {
                state.forklifts = data;
                renderForkliftTable(data);
            },
            error: function(xhr) {
                showError('Ошибка загрузки погрузчиков: ' + (xhr.responseJSON?.error || xhr.statusText));
            }
        });
    }

    // Отрисовка таблицы погрузчиков
    function renderForkliftTable(forklifts) {
        const tbody = $('#forkliftTableBody');
        tbody.empty();

        if (forklifts.length === 0) {
            tbody.html('<tr><td colspan="8" style="text-align: center; color: var(--text-muted);">Нет данных</td></tr>');
            return;
        }

        forklifts.forEach(function(forklift) {
            const tr = $('<tr>')
                .data('id', forklift.id)
                .attr('data-id', forklift.id)
                .append($('<td>').text(forklift.id))
                .append($('<td>').text(forklift.brand))
                .append($('<td>').text(forklift.number))
                .append($('<td>').text(forklift.loadCapacity))
                .append($('<td>').html('<span class="status-icon ' + (forklift.active ? 'status-active' : 'status-inactive') + '" title="' + (forklift.active ? 'Активен' : 'Неактивен') + '"></span>'))
                .append($('<td>').text(formatDateTime(forklift.modifiedAt)))
                .append($('<td>').text(forklift.modifiedBy))
                .append($('<td>').addClass('action-cell').html(
                    '<button class="action-btn" title="Редактировать"><span class="icon icon-edit"></span></button>' +
                    '<button class="action-btn" title="Удалить"><span class="icon icon-delete"></span></button>'
                ));

            if (forklift.id === state.selectedForkliftId) {
                tr.addClass('selected');
            }

            tbody.append(tr);
        });
    }

    // Загрузка простоев для выбранного погрузчика
    function loadDowntimes(forkliftId) {
        if (!forkliftId) {
            state.downtimes = [];
            renderDowntimeTable([]);
            return;
        }

        $.ajax({
            url: API_DOWNTIMES + '?forkliftId=' + forkliftId,
            method: 'GET',
            success: function(data) {
                state.downtimes = data;
                renderDowntimeTable(data);
            },
            error: function(xhr) {
                showError('Ошибка загрузки простоев: ' + (xhr.responseJSON?.error || xhr.statusText));
            }
        });
    }

    // Отрисовка таблицы простоев
    function renderDowntimeTable(downtimes) {
        const tbody = $('#downtimeTableBody');
        tbody.empty();

        if (downtimes.length === 0) {
            tbody.html('<tr><td colspan="6" style="text-align: center; color: var(--text-muted);">Нет данных</td></tr>');
            return;
        }

        downtimes.forEach(function(downtime) {
            const tr = $('<tr>')
                .data('id', downtime.id)
                .attr('data-id', downtime.id)
                .append($('<td>').text(downtime.id))
                .append($('<td>').text(formatDateTime(downtime.startTime)))
                .append($('<td>').text(downtime.endTime ? formatDateTime(downtime.endTime) : '—'))
                .append($('<td>').text(downtime.calculatedDowntime || '—'))
                .append($('<td class="description">').text(downtime.problemDescription || '—'))
                .append($('<td>').addClass('action-cell').html(
                    '<button class="action-btn" title="Редактировать"><span class="icon icon-edit"></span></button>' +
                    '<button class="action-btn" title="Удалить"><span class="icon icon-delete"></span></button>'
                ));

            if (downtime.id === state.selectedDowntimeId) {
                tr.addClass('selected');
            }

            tbody.append(tr);
        });
    }

    // Выбор строки погрузчика
    function selectForkliftRow(id) {
        state.selectedForkliftId = id;
        state.selectedDowntimeId = null;
        
        // Обновление визуального выбора
        $('#forkliftTableBody tr').removeClass('selected');
        $('#forkliftTableBody tr[data-id="' + id + '"]').addClass('selected');

        // Обновление информации о выбранном погрузчике
        const forklift = state.forklifts.find(f => f.id === id);
        if (forklift) {
            $('#selectedForkliftInfo').text(forklift.brand + ' ' + forklift.number);
        }

        // Активация кнопок
        updateButtonStates();

        // Загрузка простоев
        loadDowntimes(id);
    }

    // Выбор строки простоя
    function selectDowntimeRow(id) {
        state.selectedDowntimeId = id;
        
        // Обновление визуального выбора
        $('#downtimeTableBody tr').removeClass('selected');
        $('#downtimeTableBody tr[data-id="' + id + '"]').addClass('selected');
    }

    // Обновление состояния кнопок
    function updateButtonStates() {
        const hasSelectedForklift = state.selectedForkliftId !== null;

        $('#btnAddDowntime').prop('disabled', !hasSelectedForklift);
    }

    // Поиск
    function handleSearch() {
        const searchNumber = $('#searchNumber').val().trim();
        loadForklifts(searchNumber);
    }

    // Сброс фильтра
    function handleReset() {
        $('#searchNumber').val('');
        loadForklifts();
    }

    // Открытие модального окна погрузчика
    function openForkliftModal(mode) {
        const modal = $('#forkliftModal');
        const title = $('#forkliftModalTitle');

        if (mode === 'add') {
            title.text('Добавить погрузчик');
            $('#forkliftId').val('');
            $('#forkliftForm')[0].reset();
            $('#forkliftActive').prop('checked', true);
        } else {
            title.text('Изменить погрузчик');
            const forklift = state.forklifts.find(f => f.id === state.selectedForkliftId);
            if (forklift) {
                $('#forkliftId').val(forklift.id);
                $('#forkliftBrand').val(forklift.brand);
                $('#forkliftNumber').val(forklift.number);
                $('#forkliftLoadCapacity').val(forklift.loadCapacity);
                $('#forkliftActive').prop('checked', forklift.active);
            }
        }

        modal.addClass('show');
    }

    // Открытие модального окна простоя
    function openDowntimeModal(mode) {
        const modal = $('#downtimeModal');
        const title = $('#downtimeModalTitle');

        if (mode === 'add') {
            title.text('Добавить простой');
            $('#downtimeId').val('');
            $('#downtimeForkliftId').val(state.selectedForkliftId);
            $('#downtimeForm')[0].reset();
            // Установка текущего времени
            const now = new Date();
            now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
            $('#downtimeStartTime').val(now.toISOString().slice(0, 16));
        } else {
            title.text('Изменить простой');
            const downtime = state.downtimes.find(d => d.id === state.selectedDowntimeId);
            if (downtime) {
                $('#downtimeId').val(downtime.id);
                $('#downtimeForkliftId').val(downtime.forkliftId);
                $('#downtimeStartTime').val(formatDateTimeForInput(downtime.startTime));
                $('#downtimeEndTime').val(downtime.endTime ? formatDateTimeForInput(downtime.endTime) : '');
                $('#downtimeDescription').val(downtime.problemDescription);
            }
        }

        modal.addClass('show');
    }

    // Закрытие модального окна
    function closeModal(modalId) {
        $(modalId).removeClass('show');
    }

    // Сохранение погрузчика из модального окна
    function handleSaveForkliftFromModal() {
        const id = $('#forkliftId').val();
        const data = {
            brand: $('#forkliftBrand').val(),
            number: $('#forkliftNumber').val(),
            loadCapacity: parseFloat($('#forkliftLoadCapacity').val()),
            active: $('#forkliftActive').is(':checked'),
            modifiedBy: $('#currentUser').val() || 'Администратор'
        };

        if (!data.brand || !data.number || isNaN(data.loadCapacity)) {
            showError('Пожалуйста, заполните все обязательные поля');
            return;
        }

        const method = id ? 'PUT' : 'POST';
        const url = id ? API_FORKLIFTS + '/' + id : API_FORKLIFTS;

        $.ajax({
            url: url,
            method: method,
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function() {
                closeModal('#forkliftModal');
                loadForklifts();
                showSuccess(id ? 'Погрузчик обновлен' : 'Погрузчик добавлен');
            },
            error: function(xhr) {
                showError(xhr.responseJSON?.error || 'Ошибка сохранения');
            }
        });
    }

    // Удаление погрузчика
    function handleDeleteForklift() {
        if (!state.selectedForkliftId) return;

        if (!confirm('Удалить погрузчик? Вы уверены?')) {
            return;
        }

        $.ajax({
            url: API_FORKLIFTS + '/' + state.selectedForkliftId,
            method: 'DELETE',
            success: function() {
                state.selectedForkliftId = null;
                state.downtimes = [];
                renderDowntimeTable([]);
                $('#selectedForkliftInfo').text('Выберите погрузчик');
                loadForklifts();
                updateButtonStates();
                showSuccess('Погрузчик удален');
            },
            error: function(xhr) {
                showError(xhr.responseJSON?.error || 'Ошибка удаления');
            }
        });
    }

    // Сохранение простоя из модального окна
    function handleSaveDowntimeFromModal() {
        const id = $('#downtimeId').val();
        const data = {
            forkliftId: parseInt($('#downtimeForkliftId').val()),
            startTime: $('#downtimeStartTime').val(),
            endTime: $('#downtimeEndTime').val() || null,
            problemDescription: $('#downtimeDescription').val()
        };

        if (!data.startTime) {
            showError('Пожалуйста, укажите дату и время начала');
            return;
        }

        const method = id ? 'PUT' : 'POST';
        const url = id ? API_DOWNTIMES + '/' + id : API_DOWNTIMES;

        $.ajax({
            url: url,
            method: method,
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function() {
                closeModal('#downtimeModal');
                loadDowntimes(state.selectedForkliftId);
                showSuccess(id ? 'Простой обновлен' : 'Простой добавлен');
            },
            error: function(xhr) {
                showError(xhr.responseJSON?.error || 'Ошибка сохранения');
            }
        });
    }

    // Удаление простоя
    function handleDeleteDowntime() {
        if (!state.selectedDowntimeId) return;

        if (!confirm('Удалить информацию о простое? Вы уверены?')) {
            return;
        }

        $.ajax({
            url: API_DOWNTIMES + '/' + state.selectedDowntimeId,
            method: 'DELETE',
            success: function() {
                state.selectedDowntimeId = null;
                loadDowntimes(state.selectedForkliftId);
                updateButtonStates();
                showSuccess('Простой удален');
            },
            error: function(xhr) {
                showError(xhr.responseJSON?.error || 'Ошибка удаления');
            }
        });
    }

    // Обработка кнопки "Сохранить" (режим редактирования в таблице)
    function handleSaveForklift() {
        // Здесь можно добавить inline редактирование
        showError('Используйте модальное окно для редактирования');
    }

    // Обработка кнопки "Отменить"
    function handleCancelForklift() {
        if (!confirm('Не сохранять внесенные изменения? Вы уверены?')) {
            return;
        }
        // Сброс изменений
        loadForklifts();
    }

    // Форматирование даты и времени
    function formatDateTime(dateTimeStr) {
        if (!dateTimeStr) return '—';
        const date = new Date(dateTimeStr);
        return date.toLocaleString('ru-RU', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    // Форматирование даты для input datetime-local
    function formatDateTimeForInput(dateTimeStr) {
        if (!dateTimeStr) return '';
        const date = new Date(dateTimeStr);
        const offset = date.getTimezoneOffset();
        date.setMinutes(date.getMinutes() - offset);
        return date.toISOString().slice(0, 16);
    }

    // Показ сообщения об успехе
    function showSuccess(message) {
        // Можно реализовать более красивый toast
        alert(message);
    }

    // Показ сообщения об ошибке
    function showError(message) {
        alert(message);
    }
});
