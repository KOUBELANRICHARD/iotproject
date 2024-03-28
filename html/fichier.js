$(document).ready(function() {
    // Fonction pour mettre à jour les données sur les pages HTML
    function updateData(elementId, data) {
        // Créer une table
        var table = $('<table>').addClass('table table-bordered');

        // Ajouter l'en-tête de la table
        var thead = $('<thead>').appendTo(table);
        var trHead = $('<tr>').appendTo(thead);
        $.each(data[0], function(key, value) {
            $('<th>').text(key).appendTo(trHead);
        });

        // Ajouter les données à la table
        var tbody = $('<tbody>').appendTo(table);
        $.each(data, function(index, item) {
            var tr = $('<tr>').appendTo(tbody);
            $.each(item, function(key, value) {
                $('<td>').text(value).appendTo(tr);
            });
        });

        // Mettre à jour l'élément HTML avec la table
        $('#' + elementId).html(table); 
    }

    // Requêtes AJAX pour récupérer les données de chaque capteur
    $.ajax({
        url: 'http://localhost:8080/son',
        method: 'GET',
        dataType: 'json',
        success: function(data) {
            updateData('son-data', data);
        },
        error: function(error) {
            console.error('Erreur lors de la récupération des données du capteur de son', error);
        }
    });

    $.ajax({
        url: 'http://localhost:8080/equipements',
        method: 'GET',
        dataType: 'json',
        success: function(data) {
            updateData('equipements-data', data);
        },
        error: function(error) {
            console.error('Erreur lors de la récupération des données du capteur de son', error);
        }
    });
    $.ajax({
        url: 'http://localhost:8080/lumiere',
        method: 'GET',
        dataType: 'json',
        success: function(data) {
            updateData('lumiere-data', data);
        },
        error: function(error) {
            console.error('Erreur lors de la récupération des données du capteur de lumière', error);
        }
    });

    $.ajax({
        url: 'http://localhost:8080/moisisure',
        method: 'GET',
        dataType: 'json',
        success: function(data) {
            updateData('moisisure-data', data);
        },
        error: function(error) {
            console.error('Erreur lors de la récupération des données du capteur de moisissure', error);
        }
    });
});


 