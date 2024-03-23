<?php

namespace App\Filament\Resources\CaptorResource\Pages;

use App\Filament\Resources\CaptorResource;
use Filament\Pages\Actions;
use Filament\Resources\Pages\ListRecords;

class ListCaptors extends ListRecords
{
    protected static string $resource = CaptorResource::class;

    protected function getActions(): array
    {
        return [
            Actions\CreateAction::make(),
        ];
    }
}
