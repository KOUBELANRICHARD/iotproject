<?php

namespace App\Filament\Resources\CaptorResource\Pages;

use App\Filament\Resources\CaptorResource;
use Filament\Pages\Actions;
use Filament\Resources\Pages\EditRecord;

class EditCaptor extends EditRecord
{
    protected static string $resource = CaptorResource::class;

    protected function getActions(): array
    {
        return [
            Actions\DeleteAction::make(),
        ];
    }
}
