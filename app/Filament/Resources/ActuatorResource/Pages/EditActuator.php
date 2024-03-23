<?php

namespace App\Filament\Resources\ActuatorResource\Pages;

use App\Filament\Resources\ActuatorResource;
use Filament\Pages\Actions;
use Filament\Resources\Pages\EditRecord;

class EditActuator extends EditRecord
{
    protected static string $resource = ActuatorResource::class;

    protected function getActions(): array
    {
        return [
            Actions\DeleteAction::make(),
        ];
    }
}
