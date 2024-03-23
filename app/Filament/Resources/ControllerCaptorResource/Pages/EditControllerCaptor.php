<?php

namespace App\Filament\Resources\ControllerCaptorResource\Pages;

use App\Filament\Resources\ControllerCaptorResource;
use Filament\Pages\Actions;
use Filament\Resources\Pages\EditRecord;

class EditControllerCaptor extends EditRecord
{
    protected static string $resource = ControllerCaptorResource::class;

    protected function getActions(): array
    {
        return [
            Actions\DeleteAction::make(),
        ];
    }
}
