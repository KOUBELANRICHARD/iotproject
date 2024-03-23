<?php

namespace App\Filament\Resources\ControllerActuatorResource\Pages;

use App\Filament\Resources\ControllerActuatorResource;
use Filament\Pages\Actions;
use Filament\Resources\Pages\ListRecords;

class ListControllerActuators extends ListRecords
{
    protected static string $resource = ControllerActuatorResource::class;

    protected function getActions(): array
    {
        return [
            Actions\CreateAction::make(),
        ];
    }
}
