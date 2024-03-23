<?php

namespace App\Filament\Resources\ControllerCaptorResource\Pages;

use App\Filament\Resources\ControllerCaptorResource;
use Filament\Pages\Actions;
use Filament\Resources\Pages\ListRecords;

class ListControllerCaptors extends ListRecords
{
    protected static string $resource = ControllerCaptorResource::class;

    protected function getActions(): array
    {
        return [
            Actions\CreateAction::make(),
        ];
    }
}
